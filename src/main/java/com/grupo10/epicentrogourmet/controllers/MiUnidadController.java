package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.Plato;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/mi-unidad")
public class MiUnidadController {

    private final IUnidadVentaService unidadVentaService;

    public MiUnidadController(IUnidadVentaService unidadVentaService) {
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping
    public ModelAndView miUnidad() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        UnidadVenta unidad = unidadVentaService.findByCorreoResponsable(correo);

        ModelAndView mav = new ModelAndView("unidad-venta/mi-unidad");
        mav.addObject("unidad", unidad);
        mav.addObject("plato", new Plato());
        return mav;
    }

    @PostMapping("/plato/agregar")
    public ModelAndView agregarPlato(@ModelAttribute("plato") Plato plato) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UnidadVenta unidad = unidadVentaService.findByCorreoResponsable(auth.getName());

        unidadVentaService.agregarPlato(unidad.getId(), plato);
        return new ModelAndView("redirect:/mi-unidad?platoSuccess=true");
    }

    @GetMapping("/plato/eliminar/{id}")
    public ModelAndView eliminarPlato(@PathVariable Long id) {
        unidadVentaService.eliminarPlato(id);
        return new ModelAndView("redirect:/mi-unidad?platoDeleted=true");
    }

    @GetMapping("/plato/editar/{id}")
    public ModelAndView editarPlatoForm(@PathVariable Long id) {
        Plato plato = unidadVentaService.findPlatoById(id);
        ModelAndView mav = new ModelAndView("unidad-venta/editar-plato");
        mav.addObject("plato", plato);
        return mav;
    }

    @PostMapping("/plato/editar/{id}")
    public ModelAndView editarPlato(@PathVariable Long id, @ModelAttribute("plato") Plato plato) {
        unidadVentaService.modificarPlato(id, plato);
        return new ModelAndView("redirect:/mi-unidad?platoEdited=true");
    }




}