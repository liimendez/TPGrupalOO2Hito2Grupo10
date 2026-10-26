package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.Costos;
import com.grupo10.epicentrogourmet.dtos.DetalleCanonDTO;

public interface ICostosService {
    List<Costos> getAll();
    Optional<Costos> findById(Long id);
    Costos insertOrUpdate(Costos costos);
    boolean remove(Long id);

    // para responsable -  CostosDTO como base de precios
    DetalleCanonDTO calcularCanon(Long unidadVentaId);
}