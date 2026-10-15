package com.grupo10.epicentrogourmet.services;


import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.Plato;

public interface IPlatoService {
    List<Plato> getAll();
    Optional<Plato> findById(Long id);
    Plato insertOrUpdate(Plato plato);
    boolean remove(Long id);
    List<Plato> findByUnidadVentaId(Long idUnidad);
}