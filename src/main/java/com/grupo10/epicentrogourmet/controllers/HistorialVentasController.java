package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.dto.VentaDiariaDTO;
import com.grupo10.epicentrogourmet.entities.Festival;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IFestivalRepository;
import com.grupo10.epicentrogourmet.services.IHistorialVentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/responsable")
public class HistorialVentasController {

    @Autowired
    private IHistorialVentasService historialVentasService;

    @Autowired
    private IFestivalRepository festivalRepository;

    @GetMapping("/historial")
    public String historial(@RequestParam Long festivalId,
                            @RequestParam Long unidadVentaId,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                            Model model, Authentication auth) {

        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));

        // Busca la unidad dentro del festival y valida que sea del responsable logueado
        UnidadVenta unidad = festival.getUnidadesVenta().stream()
                .filter(u -> unidadVentaId.equals(u.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada en este festival"));

        String correo = auth.getName();
        if (unidad.getResponsable() == null || !correo.equals(unidad.getResponsable().getCorreo())) {
            throw new IllegalArgumentException("No tenés permiso para ver esta unidad");
        }

        List<VentaDiariaDTO> historial = historialVentasService.obtenerHistorial(festivalId, unidadVentaId, desde, hasta);
        double totalGeneral = historial.stream().mapToDouble(VentaDiariaDTO::getTotalDia).sum();
        int totalPedidos = historial.stream().mapToInt(VentaDiariaDTO::getCantidadPedidos).sum();

        model.addAttribute("festival", festival);
        model.addAttribute("unidad", unidad);
        model.addAttribute("historial", historial);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("totalGeneral", totalGeneral);
        model.addAttribute("totalPedidos", totalPedidos);
        return "responsable/historial";
    }
}