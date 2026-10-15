package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.Pedido;

public interface IPedidoService {
    List<Pedido> getAll();
    Optional<Pedido> findById(Long id);
    Pedido insertOrUpdate(Pedido pedido);
    boolean remove(Long id);
}