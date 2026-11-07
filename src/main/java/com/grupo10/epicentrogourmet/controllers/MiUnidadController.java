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
    public ModelAndView miUnidad(@RequestParam(value = "platoError", required = false) String platoError) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        UnidadVenta unidad = unidadVentaService.findByCorreoResponsable(correo);

        ModelAndView mav = new ModelAndView("unidad-venta/mi-unidad");
        mav.addObject("unidad", unidad);
        mav.addObject("plato", new Plato());
        if (platoError != null) {
            mav.addObject("platoError", traducirError(platoError));
        }
        return mav;
    }

    @PostMapping("/plato/agregar")
    public ModelAndView agregarPlato(@ModelAttribute("plato") Plato plato) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UnidadVenta unidad = unidadVentaService.findByCorreoResponsable(auth.getName());

        String error = validarPlato(plato);
        if (error != null) {
            return new ModelAndView("redirect:/mi-unidad?platoError=" + error);
        }

        unidadVentaService.agregarPlato(unidad.getId(), plato);
        return new ModelAndView("redirect:/mi-unidad?platoSuccess=true");
    }

    @GetMapping("/plato/eliminar/{id}")
    public ModelAndView eliminarPlato(@PathVariable("id") Long id) {
        unidadVentaService.eliminarPlato(id);
        return new ModelAndView("redirect:/mi-unidad?platoDeleted=true");
    }

    @GetMapping("/plato/editar/{id}")
    public ModelAndView editarPlatoForm(@PathVariable("id") Long id) {
        Plato plato = unidadVentaService.findPlatoById(id);
        ModelAndView mav = new ModelAndView("unidad-venta/editar-plato");
        mav.addObject("plato", plato);
        mav.addObject("origen", "mi-unidad");
        return mav;
    }

    @PostMapping("/plato/editar/{id}")
    public ModelAndView editarPlato(@PathVariable("id") Long id, @ModelAttribute("plato") Plato plato) {
        String error = validarPlato(plato);
        if (error != null) {
            return new ModelAndView("redirect:/mi-unidad?platoError=" + error);
        }
        unidadVentaService.modificarPlato(id, plato);
        return new ModelAndView("redirect:/mi-unidad?platoEdited=true");
    }


    private String validarPlato(Plato plato) {
        if (plato.getNombre() == null || plato.getNombre().trim().isEmpty()) {
            return "nombreVacio";
        }
        if (plato.getPrecioVenta() <= 0) {
            return "precioInvalido";
        }
        if (plato.getCostoProduccion() <= 0) {
            return "costoInvalido";
        }
        if (plato.getCostoProduccion() >= plato.getPrecioVenta()) {
            return "costoMayorQuePrecio";
        }
        return null;
    }


    private String traducirError(String codigo) {
        switch (codigo) {
            case "nombreVacio": return "El nombre del plato es obligatorio";
            case "precioInvalido": return "El precio de venta debe ser mayor a 0";
            case "costoInvalido": return "El costo de producción debe ser mayor a 0. No puede ser $0.";
            case "costoMayorQuePrecio": return "El costo no puede ser mayor o igual al precio. Estarías perdiendo dinero.";
            default: return "Error al guardar el plato";
        }
    }
}