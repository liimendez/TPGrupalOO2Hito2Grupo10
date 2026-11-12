package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.MetodoPago;
import com.grupo10.epicentrogourmet.services.IMetodoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/metodos-pago")
public class MetodoPagoController {

    @Autowired
    private IMetodoPagoService metodoPagoService;

    @GetMapping("")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String listar(Model model) {
        if (!model.containsAttribute("metodoPago")) {
            model.addAttribute("metodoPago", new MetodoPago());
        }
        model.addAttribute("metodosPago", metodoPagoService.getAll());
        return "responsable/metodo-pago";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return metodoPagoService.findById(id)
                .map(metodo -> {
                    model.addAttribute("metodoPago", metodo);
                    model.addAttribute("metodosPago", metodoPagoService.getAll());
                    return "responsable/metodo-pago";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("error", "El método de pago no existe.");
                    return "redirect:/metodos-pago";
                });
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String guardar(@ModelAttribute MetodoPago metodoPago, RedirectAttributes flash) {
        try {
            metodoPagoService.insertOrUpdate(metodoPago);
            flash.addFlashAttribute("success", "Método de pago guardado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/metodos-pago";
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            metodoPagoService.remove(id);
            flash.addFlashAttribute("success", "Método de pago eliminado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el método de pago.");
        }
        return "redirect:/metodos-pago";
    }
}