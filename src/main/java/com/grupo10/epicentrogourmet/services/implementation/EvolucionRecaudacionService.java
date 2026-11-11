package com.grupo10.epicentrogourmet.services.implementation;

import com.grupo10.epicentrogourmet.dtos.EvolucionRecaudacionDTO;
import com.grupo10.epicentrogourmet.dto.VentaDiariaDTO;
import com.grupo10.epicentrogourmet.services.IHistorialVentasService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EvolucionRecaudacionService {

    private final IHistorialVentasService historialVentasService;

    public EvolucionRecaudacionService(IHistorialVentasService historialVentasService) {
        this.historialVentasService = historialVentasService;
    }

    public List<EvolucionRecaudacionDTO> getEvolucion(Long festivalId, Long unidadVentaId, LocalDate desde, LocalDate hasta) {
        List<VentaDiariaDTO> historial = historialVentasService.obtenerHistorial(festivalId, unidadVentaId, desde, hasta);
        List<EvolucionRecaudacionDTO> evolucion = new ArrayList<>();
        Double anterior = null;
        for (VentaDiariaDTO dia : historial) {
            Double variacion = null;
            if (anterior != null) {
                variacion = dia.getTotalDia() - anterior;
            }
            evolucion.add(new EvolucionRecaudacionDTO(
                    dia.getFecha(),
                    dia.getCantidadPedidos(),
                    dia.getTotalDia(),
                    variacion
            ));
            anterior = dia.getTotalDia();
        }
        return evolucion;
    }
}