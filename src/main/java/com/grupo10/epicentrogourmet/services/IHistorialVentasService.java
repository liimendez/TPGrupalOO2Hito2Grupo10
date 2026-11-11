package com.grupo10.epicentrogourmet.services;

import com.grupo10.epicentrogourmet.dto.VentaDiariaDTO;
import java.time.LocalDate;
import java.util.List;

public interface IHistorialVentasService {
    List<VentaDiariaDTO> obtenerHistorial(Long festivalId, Long unidadVentaId, LocalDate desde, LocalDate hasta);
}