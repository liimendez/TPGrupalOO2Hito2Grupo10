package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.dtos.RequestUnidadVentaDTO;

public interface IUnidadVentaService {
    List<UnidadVenta> getAll();
    Optional<UnidadVenta> findById(Long id);
    UnidadVenta insertOrUpdate(UnidadVenta unidadVenta);
    boolean remove(Long id);

    // logica
    String generarCodigoUnico(UnidadVenta unidadVenta);
    boolean isCodigoValido(String codigoUnico);
    double calcularCanon(Long idUnidadVenta);

    void registrarUnidadConResponsable(RequestUnidadVentaDTO dto);
}