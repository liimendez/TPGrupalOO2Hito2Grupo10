package com.grupo10.epicentrogourmet.services.implementation;

import com.grupo10.epicentrogourmet.dtos.ReporteVentaDTO;
import com.grupo10.epicentrogourmet.entities.Pedido;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IPedidoRepository;
import com.grupo10.epicentrogourmet.services.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("pedidoService")
public class PedidoService implements IPedidoService {

    @Autowired
    private IPedidoRepository pedidoRepository;

    @Override
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Pedido insertOrUpdate(Pedido pedido) {
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

        double recaudacionTotal = 0.0;

        for (Pedido pedido : pedidos) {
            if (pedido.getDetalles() != null) {
                for (var detalle : pedido.getDetalles()) {
                    if (detalle.getPlato() != null) {
                        recaudacionTotal += detalle.getCantidad() * detalle.getPlato().getPrecioVenta();
                    }
                }
            }
        }

        long cantidadPedidos = pedidos.size();

        return new ReporteVentaDTO(unidadVenta.getId(), unidadVenta.getNombreComercial(), recaudacionTotal, cantidadPedidos);
    }
}