package com.grupo10.epicentrogourmet.services.implementation;

import com.grupo10.epicentrogourmet.dtos.DetallePedidoDTO;
import com.grupo10.epicentrogourmet.dtos.PedidoDTO;
import com.grupo10.epicentrogourmet.dtos.ReporteVentaDTO;
import com.grupo10.epicentrogourmet.entities.Festival;
import com.grupo10.epicentrogourmet.entities.Pedido;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IPedidoRepository;
import com.grupo10.epicentrogourmet.services.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("pedidoService")
public class PedidoService implements IPedidoService {

    @Autowired
    private IPedidoRepository pedidoRepository;

    @Override
    public List<Pedido> getAll() { return pedidoRepository.findAll(); }

    @Override
    public Optional<Pedido> findById(Long id) { return pedidoRepository.findById(id); }

    @Override
    public Pedido insertOrUpdate(Pedido pedido) { return pedidoRepository.save(pedido); }

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
        // ahora usamos tu getTotal() que ya está en la entity
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

        var listaDetalles = pedido.getDetalles().stream().map(d -> {
            return DetallePedidoDTO.builder()
                    .id(d.getId())
                    .cantidad(d.getCantidad())
                    .pedidoId(pedido.getId())
                    .platoId(d.getPlato().getId())
                    .nombrePlato(d.getPlato().getNombre())
                    .subtotal(d.getCantidad() * d.getPlato().getPrecioVenta())
                    .build();
        }).toList();

        dto.setDetalles(listaDetalles);
        return dto;
    }

    @Override
    public List<PedidoDTO> toDTOList(List<Pedido> pedidos) {
        return pedidos.stream().map(this::toDTO).collect(Collectors.toList());
    }
}