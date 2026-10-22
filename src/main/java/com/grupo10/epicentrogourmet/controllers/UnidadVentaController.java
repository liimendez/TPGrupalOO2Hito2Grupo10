package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.Plato;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.grupo10.epicentrogourmet.dtos.RequestUnidadVentaDTO;
import com.grupo10.epicentrogourmet.helpers.ViewRouteHelper;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;
import com.grupo10.epicentrogourmet.repositories.IPlatoRepository;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/unidad-venta")
public class UnidadVentaController {

    private final IUnidadVentaService unidadVentaService;
    private final IUsuarioRepository usuarioRepository;
    private final IPlatoRepository platoRepository;

    public UnidadVentaController(IUnidadVentaService unidadVentaService, IUsuarioRepository usuarioRepository, IPlatoRepository platoRepository) {
        this.unidadVentaService = unidadVentaService;
        this.usuarioRepository = usuarioRepository;
        this.platoRepository = platoRepository;
    }

    @GetMapping("/new")
    public ModelAndView mostrarFormulario() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UNIDAD_VENTA_FORM);
        mAV.addObject("unidadDTO", new RequestUnidadVentaDTO());
        return mAV;
    }

    @PostMapping("/save")
    public ModelAndView guardarUnidad(@ModelAttribute("unidadDTO") RequestUnidadVentaDTO dto, Model model) {
        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.UNIDAD_VENTA_FORM);
            mAV.addObject("unidadDTO", dto);
            mAV.addObject("error", "Ese correo " + dto.getCorreo() + " ya está registrado, usá otro");
            return mAV;
        }
        unidadVentaService.registrarUnidadConResponsable(dto);
        return new ModelAndView("redirect:" + ViewRouteHelper.ROUTE + "?success=true");
    }

    @GetMapping("/{id}")
    @Transactional
    public ModelAndView detalle(@PathVariable Long id) {
        UnidadVenta u = unidadVentaService.findById(id).orElseThrow();
        u.getPlatosOfrecidos().size(); // Evita LazyInitializationException
        ModelAndView mav = new ModelAndView("unidad-venta/detalle");
        mav.addObject("unidad", u);
        mav.addObject("unidadVenta", u);
        mav.addObject("platos", platoRepository.findByUnidadVenta_Id(id));
        return mav;
    }

    @PostMapping("/{id}/agregar-plato")
    @Transactional
    public String agregarPlato(@PathVariable Long id,
                               @RequestParam String nombre,
                               @RequestParam double precioVenta,
                               @RequestParam double costoProduccion) {
        Plato p = new Plato();
        p.setNombre(nombre);
        p.setPrecioVenta(precioVenta);
        p.setCostoProduccion(costoProduccion);
        unidadVentaService.agregarPlato(id, p);
        return "redirect:/unidad-venta/" + id;
    }

    @GetMapping("/{id}/eliminar-plato/{platoId}")
    public String eliminarPlato(@PathVariable Long id, @PathVariable Long platoId) {
        unidadVentaService.eliminarPlato(platoId);
        return "redirect:/unidad-venta/" + id;
    }
}