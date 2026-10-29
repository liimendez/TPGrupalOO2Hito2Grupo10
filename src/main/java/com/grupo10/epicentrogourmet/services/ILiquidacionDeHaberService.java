package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.LiquidacionDeHaber;

public interface ILiquidacionDeHaberService {
    List<LiquidacionDeHaber> getAll();
    Optional<LiquidacionDeHaber> findById(Long id);
    LiquidacionDeHaber insertOrUpdate(LiquidacionDeHaber liquidacion);
    boolean remove(Long id);
    List<LiquidacionDeHaber> findByPersonalId(Long personalId);

    // FORMULAS
    double calcularTotalACobrar(Long idPersonal, Long idUnidadVenta);
    LiquidacionDeHaber liquidar(Long idPersonal, Long idUnidadVenta);

    byte[] generarReciboSueldoPdf(Long liquidacionId);
}