package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.services.ICostosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/responsable")
@PreAuthorize("hasAnyAuthority('RESPONSABLE','ROLE_RESPONSABLE')") // <-- FIX: solo RESPONSABLE
public class ResponsableController {

    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @Autowired @Qualifier("costosService") private ICostosService costosService;

    // === FIX SEGURIDAD ===
    private void validarQueEsMiUnidad(Long idUnidadUrl, String correo) {
        UnidadVenta unidad = unidadVentaRepository.findById(idUnidadUrl).orElse(null);
        if (unidad == null || unidad.getResponsable() == null || !correo.equals(unidad.getResponsable().getCorreo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No podés ver el canon de la unidad " + idUnidadUrl + " - no es tuya.");
        }
    }

    @GetMapping("/unidades")
    public String misUnidades(Authentication auth, Model model){
        String correo = auth.getName();
        var unidades = unidadVentaRepository.findAll().stream()
                .filter(u -> u.getResponsable()!=null && correo.equals(u.getResponsable().getCorreo()))
                .toList();
        model.addAttribute("unidades", unidades);
        return "responsable/unidades";
    }

    @GetMapping("/canon/{id}")
    public String verCanon(@PathVariable Long id, Model model, Authentication auth){
        validarQueEsMiUnidad(id, auth.getName()); // <-- FIX: solo el dueño de esa unidad ve su canon
        model.addAttribute("detalle", costosService.calcularCanon(id));
        return "responsable/canon";
    }
}