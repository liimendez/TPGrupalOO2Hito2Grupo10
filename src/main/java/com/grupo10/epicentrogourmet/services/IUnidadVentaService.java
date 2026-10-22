package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;

import com.grupo10.epicentrogourmet.entities.Plato;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.dtos.RequestUnidadVentaDTO;

public interface IUnidadVentaService {
    List<UnidadVenta> getAll();
    Optional<UnidadVenta> findById(Long id);
    UnidadVenta insertOrUpdate(UnidadVenta unidadVenta);
    UnidadVenta findByCorreoResponsable(String correo);

    boolean remove(Long id);

    // logica
    String generarCodigoUnico(UnidadVenta unidadVenta);
    boolean isCodigoValido(String codigoUnico);
    double calcularCanon(Long idUnidadVenta);

    void registrarUnidadConResponsable(RequestUnidadVentaDTO dto);
   // platos
    void agregarPlato(Long unidadId, Plato plato);
    void eliminarPlato(Long platoId);
    Plato findPlatoById(Long id);
    void modificarPlato(Long id, Plato platoActualizado);


}