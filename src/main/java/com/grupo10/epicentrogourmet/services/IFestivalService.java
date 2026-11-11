package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.grupo10.epicentrogourmet.dtos.*;
import com.grupo10.epicentrogourmet.entities.Festival;

public interface IFestivalService {
    List<Festival> getAll();
    Optional<Festival> findById(Long id);
    Festival insertOrUpdate(Festival festival);
    boolean remove(Long id);
    void agregarUnidadAFestival(Long festivalId, Long unidadVentaId);
    ReporteVentaDTO getRecaudacionTotal(Long festivalId);
    RentabilidadNetaDTO getRentabilidadNeta(Long festivalId, Long unidadVentaId);
    RentabilidadNetaDTO getRentabilidadNetaEntreFechas(Long festivalId, Long unidadVentaId, java.time.LocalDate desde, java.time.LocalDate hasta);
    PlatoDTO getPlatoEstrella(Long festivalId, Long unidadVentaId);

    List<ReporteVentaDTO> getRankingUnidadesPorRecaudacion(Long festivalId);
    List<ReporteMayoresCanonDTO> getUnidadesConMayorCanon(Long festivalId, int topX);

    Optional<Festival> findByIdWithUnidades(Long id);

    Map<String, Object> getEstadoActualEmpleado(String correo);

    CierreCajaDTO getCierreCajaDiario(String correoCajero);
}