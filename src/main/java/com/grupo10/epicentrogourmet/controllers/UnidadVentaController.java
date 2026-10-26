package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.Plato;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.grupo10.epicentrogourmet.dtos.RequestUnidadVentaDTO;
import com.grupo10.epicentrogourmet.helpers.ViewRouteHelper;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;
import com.grupo10.epicentrogourmet.repositories.IPlatoRepository;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;
import java.util.Comparator;

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
    public ModelAndView guardarUnidad(@ModelAttribute("unidadDTO") RequestUnidadVentaDTO dto, org.springframework.ui.Model model) {
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
    public ModelAndView detalle(@PathVariable Long id) {
        UnidadVenta u = unidadVentaService.findByIdWithPlatos(id)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));
        var platosOrdenados = u.getPlatosOfrecidos().stream()
                .sorted(Comparator.comparing(Plato::getId)).toList();

        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean esResponsable = false;
        boolean esAdmin = false;
        boolean esCajero = false;
        if (auth != null) {
            esResponsable = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("RESPONSABLE"));
            esAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN"));
            esCajero = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("CAJERO") || a.getAuthority().contains("COCINERO"));
        }

        ModelAndView mav = new ModelAndView("unidad-venta/detalle");
        mav.addObject("unidad", u);
        mav.addObject("platos", platosOrdenados);
        mav.addObject("esResponsable", esResponsable);
        mav.addObject("esAdmin", esAdmin);
        mav.addObject("esCajero", esCajero);
        return mav;
    }

    @PostMapping("/{id}/agregar-plato")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String agregarPlato(@PathVariable Long id, @RequestParam String nombre, @RequestParam double costoProduccion, @RequestParam double precioVenta, @RequestParam(required = false) String imagenUrl) {
        Plato plato = new Plato();
        plato.setNombre(nombre);
        plato.setCostoProduccion(costoProduccion);
        plato.setPrecioVenta(precioVenta);
        plato.setImagenUrl(imagenUrl);
        unidadVentaService.agregarPlato(id, plato);
        return "redirect:/unidad-venta/" + id;
    }

    @PostMapping("/{id}/eliminar-plato/{platoId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String eliminarPlato(@PathVariable Long id, @PathVariable Long platoId) {
        unidadVentaService.eliminarPlato(platoId);
        return "redirect:/unidad-venta/" + id;
    }

    @GetMapping("/plato/editar/{platoId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    @Transactional
    public ModelAndView editarPlatoForm(@PathVariable Long platoId,
                                        @RequestParam(value="origen", required=false) String origen) {
        Plato plato = platoRepository.findByIdWithUnidad(platoId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado " + platoId));
        ModelAndView mav = new ModelAndView("unidad-venta/editar-plato");
        mav.addObject("plato", plato);
        mav.addObject("origen", origen);
        return mav;
    }

    @PostMapping("/plato/editar/{platoId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    @Transactional
    public String editarPlato(@PathVariable Long platoId,
                              @RequestParam String nombre,
                              @RequestParam double costoProduccion,
                              @RequestParam double precioVenta,
                              @RequestParam(required = false) String imagenUrl,
                              @RequestParam(value="origen", required=false) String origen) {
        Plato actualizado = new Plato();
        actualizado.setNombre(nombre);
        actualizado.setCostoProduccion(costoProduccion);
        actualizado.setPrecioVenta(precioVenta);
        actualizado.setImagenUrl(imagenUrl);
        unidadVentaService.modificarPlato(platoId, actualizado);
        if("mi-unidad".equals(origen)) return "redirect:/mi-unidad?platoEdited=true";
        return "redirect:/unidad-venta/" + platoRepository.findByIdWithUnidad(platoId).get().getUnidadVenta().getId();
    }
}