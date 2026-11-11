package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.dtos.EvolucionRecaudacionDTO;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IFestivalRepository;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.services.implementation.EvolucionRecaudacionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/responsable")
@PreAuthorize("hasRole('RESPONSABLE')")
public class EvolucionRecaudacionController {

    private final EvolucionRecaudacionService evolucionService;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final IFestivalRepository festivalRepository;

    public EvolucionRecaudacionController(EvolucionRecaudacionService evolucionService,
                                          IUnidadVentaRepository unidadVentaRepository,
                                          IFestivalRepository festivalRepository) {
        this.evolucionService = evolucionService;
        this.unidadVentaRepository = unidadVentaRepository;
        this.festivalRepository = festivalRepository;
    }

    @GetMapping("/evolucion-recaudacion")
    public String evolucion(
            @RequestParam Long unidadVentaId,
            @RequestParam(required = false) Long festivalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Authentication auth,
            Model model) {

        String correo = auth.getName();
        UnidadVenta unidad = unidadVentaRepository.findById(unidadVentaId).orElse(null);
        if (unidad == null || unidad.getResponsable() == null || !correo.equals(unidad.getResponsable().getCorreo())) {
            return "redirect:/responsable/unidades?error=no-autorizado";
        }

        List<EvolucionRecaudacionDTO> evolucion = evolucionService.getEvolucion(festivalId, unidadVentaId, desde, hasta);

        double total = evolucion.stream().mapToDouble(EvolucionRecaudacionDTO::getTotalDia).sum();
        EvolucionRecaudacionDTO mejor = evolucion.stream().max(Comparator.comparingDouble(EvolucionRecaudacionDTO::getTotalDia)).orElse(null);
        EvolucionRecaudacionDTO peor = evolucion.stream().min(Comparator.comparingDouble(EvolucionRecaudacionDTO::getTotalDia)).orElse(null);

        String nombreUnidad = unidad.getNombreComercial();
        String nombreFestival = "";
        if (festivalId != null) {
            nombreFestival = festivalRepository.findById(festivalId)
                    .map(f -> f.getNombre()).orElse("");
        }

        model.addAttribute("evolucion", evolucion);
        model.addAttribute("total", total);
        model.addAttribute("mejor", mejor);
        model.addAttribute("peor", peor);
        model.addAttribute("unidadVentaId", unidadVentaId);
        model.addAttribute("festivalId", festivalId);
        model.addAttribute("nombreUnidad", nombreUnidad);
        model.addAttribute("nombreFestival", nombreFestival);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "responsable/evolucion-recaudacion";
    }
}