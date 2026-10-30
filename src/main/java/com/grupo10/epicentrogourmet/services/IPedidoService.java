package com.grupo10.epicentrogourmet.services;

import com.grupo10.epicentrogourmet.dtos.PedidoDTO;
import com.grupo10.epicentrogourmet.dtos.ReporteVentaDTO;
import com.grupo10.epicentrogourmet.entities.Pedido;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;

import java.util.List;
import java.util.Optional;

public interface IPedidoService {
    List<Pedido> getAll();
    Optional<Pedido> findById(Long id);
    Pedido insertOrUpdate(Pedido pedido);
    boolean remove(Long id);

    ReporteVentaDTO generarReportePorUnidadVenta(UnidadVenta unidadVenta);


    String resolverNombreFestival(Pedido pedido);
    PedidoDTO toDTO(Pedido pedido);
    List<PedidoDTO> toDTOList(List<Pedido> pedidos);
}