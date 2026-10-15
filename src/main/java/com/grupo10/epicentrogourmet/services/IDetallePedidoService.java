package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.DetallePedido;

public interface IDetallePedidoService {
    List<DetallePedido> getAll();
    Optional<DetallePedido> findById(Long id);
    DetallePedido insertOrUpdate(DetallePedido detalle);
    boolean remove(Long id);
}
