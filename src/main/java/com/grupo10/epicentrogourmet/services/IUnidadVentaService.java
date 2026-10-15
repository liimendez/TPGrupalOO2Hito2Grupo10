package com.grupo10.epicentrogourmet.services;

import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import org.hibernate.internal.util.Optional;

import java.util.List;

public interface IUnidadVentaService {
    List<UnidadVenta> getAll();
    Optional<UnidadVenta> findById(Long id);
    UnidadVenta findByCodigoUnico(String codigoUnico);
    UnidadVenta insertOrUpdate(UnidadVenta unidadVenta);
    boolean remove(Long id);
    List<UnidadVenta> findByFestivalId(Long idFestival);
}