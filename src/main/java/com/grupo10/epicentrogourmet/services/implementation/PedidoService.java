package com.grupo10.epicentrogourmet.services.implementation;

import com.grupo10.epicentrogourmet.dtos.DetallePedidoDTO;
import com.grupo10.epicentrogourmet.dtos.PedidoDTO;
import com.grupo10.epicentrogourmet.dtos.ReporteVentaDTO;
import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.repositories.IMetodoPagoRepository;
import com.grupo10.epicentrogourmet.repositories.IPedidoRepository;
import com.grupo10.epicentrogourmet.repositories.IPlatoRepository;
import com.grupo10.epicentrogourmet.services.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("pedidoService")
public class PedidoService implements IPedidoService {

    @Autowired
    private IPedidoRepository pedidoRepository;
    @Autowired
    private IPlatoRepository platoRepository;

    @Autowired
    private IMetodoPagoRepository metodoPagoRepository;

    @Override
    public List<Pedido> getAll() { return pedidoRepository.findAll(); }

    @Override
    public Optional<Pedido> findById(Long id) { return pedidoRepository.findById(id); }

    @Override
    @Transactional
    public Pedido insertOrUpdate(Pedido pedido) {

        List<MetodoPago> metodosActivos = metodoPagoRepository.findByActivoTrue();

        if (!metodosActivos.isEmpty()) {
            if (pedido.getMetodoPago() == null || pedido.getMetodoPago().getId() == null) {
                throw new IllegalArgumentException("Debe seleccionar un método de pago.");
            }
        } else {
            pedido.setMetodoPago(null);
        }

        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                if (detalle.getPlato() != null && detalle.getPlato().getId() != null) {

                    Plato platoBD = platoRepository.findById(detalle.getPlato().getId())
                            .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

                    verificarYDescontarStock(platoBD, detalle.getCantidad());

                    platoRepository.save(platoBD);
                    detalle.setPlato(platoBD);
                }
            }
        }
        return pedidoRepository.save(pedido);
    }

    @Override
    public boolean remove(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ReporteVentaDTO generarReportePorUnidadVenta(UnidadVenta unidadVenta) {
        List<Pedido> pedidos = pedidoRepository.generarReportePorUnidadVenta(unidadVenta);
        double recaudacionTotal = pedidos.stream().mapToDouble(Pedido::getTotal).sum();
        return new ReporteVentaDTO(unidadVenta.getId(), unidadVenta.getNombreComercial(), recaudacionTotal, pedidos.size());
    }

    @Override
    public String resolverNombreFestival(Pedido pedido) {
        if (pedido == null || pedido.getUnidadVenta() == null
                || pedido.getUnidadVenta().getFestivales() == null
                || pedido.getUnidadVenta().getFestivales().isEmpty()
                || pedido.getFechaPedido() == null) {
            return "EPICENTRO GOURMET";
        }
        return pedido.getUnidadVenta().getFestivales().stream()
                .filter(f -> !pedido.getFechaPedido().isBefore(f.getFechaInicio())
                        && !pedido.getFechaPedido().isAfter(f.getFechaFin()))
                .map(Festival::getNombre)
                .findFirst()
                .orElse(pedido.getUnidadVenta().getFestivales().stream()
                        .findFirst().map(Festival::getNombre).orElse("EPICENTRO GOURMET"));
    }

    @Override
    public PedidoDTO toDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setUnidadVentaId(pedido.getUnidadVenta().getId());
        dto.setNombreUnidad(pedido.getUnidadVenta().getNombreComercial());
        dto.setCodigoUnico(pedido.getUnidadVenta().getCodigoUnico());
        dto.setNombreFestival(resolverNombreFestival(pedido));
        dto.setTotal(pedido.getTotal());
        dto.setCostoTotal(pedido.getCostoTotal());
        dto.setCantidadDetalles(pedido.getDetalles().size());

        if (pedido.getMetodoPago() != null) {
            dto.setMetodoPagoNombre(pedido.getMetodoPago().getNombre());
            dto.setMetodoPagoDescuento(pedido.getMetodoPago().getDescuentoPorcentaje());

            double subtotalSinDescuento = pedido.getDetalles().stream().mapToDouble(d -> d.getSubtotal()).sum();
            double descuentoCalculado = subtotalSinDescuento - pedido.getTotal();
            dto.setMontoDescuento(descuentoCalculado);
        }

        var listaDetalles = pedido.getDetalles().stream().map(d -> {
            return DetallePedidoDTO.builder()
                    .id(d.getId())
                    .cantidad(d.getCantidad())
                    .pedidoId(pedido.getId())
                    .platoId(d.getPlato().getId())
                    .nombrePlato(d.getPlato().getNombre())
                    .subtotal(d.getSubtotal())
                    .precioUnitario(d.getPrecioUnitario())
                    .build();
        }).toList();

        dto.setDetalles(listaDetalles);
        return dto;
    }

    @Override
    public List<PedidoDTO> toDTOList(List<Pedido> pedidos) {
        return pedidos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void verificarYDescontarStock(Plato plato, int cantidadPedida) {
        if (plato.getStockPlato() != null) {
            if (plato.getStockPlato() < cantidadPedida) {
                throw new IllegalArgumentException("Stock insuficiente para: " + plato.getNombre() +
                        ". Disponible: " + plato.getStockPlato() + ", solicitado: " + cantidadPedida);
            }
            plato.setStockPlato(plato.getStockPlato() - cantidadPedida);
        }
    }

}