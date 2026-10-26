package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.services.ICostosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/responsable")
public class ResponsableController {

    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @Autowired @Qualifier("costosService") private ICostosService costosService;

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
    public String verCanon(@PathVariable Long id, Model model){
        model.addAttribute("detalle", costosService.calcularCanon(id));
        return "responsable/canon";
    }
}