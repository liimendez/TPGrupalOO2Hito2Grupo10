package com.grupo10.epicentrogourmet.services;

import com.grupo10.epicentrogourmet.entities.MetodoPago;
import java.util.List;
import java.util.Optional;

public interface IMetodoPagoService {
    List<MetodoPago> getAll();
    List<MetodoPago> getActivos();
    Optional<MetodoPago> findById(Long id);
    MetodoPago insertOrUpdate(MetodoPago metodoPago);
    boolean remove(Long id);
}