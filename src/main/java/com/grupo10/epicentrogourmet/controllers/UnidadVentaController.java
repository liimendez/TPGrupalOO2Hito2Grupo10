package com.grupo10.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.grupo10.epicentrogourmet.dtos.RequestUnidadVentaDTO;
import com.grupo10.epicentrogourmet.helpers.ViewRouteHelper;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;

@Controller
@RequestMapping("/unidad-venta")
public class UnidadVentaController {

    private final IUnidadVentaService unidadVentaService;

    public UnidadVentaController(IUnidadVentaService unidadVentaService) {
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("/new")
    public ModelAndView mostrarFormulario() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UNIDAD_VENTA_FORM);
        mAV.addObject("unidadDTO", new RequestUnidadVentaDTO());
        return mAV;
    }

    @PostMapping("/save")
    public ModelAndView guardarUnidad(@ModelAttribute("unidadDTO") RequestUnidadVentaDTO dto) {
        unidadVentaService.registrarUnidadConResponsable(dto);
        return new ModelAndView("redirect:" + ViewRouteHelper.ROUTE + "?success=true");
    }
}