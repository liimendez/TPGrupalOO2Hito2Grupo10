package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.Personal;
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
import com.grupo10.epicentrogourmet.repositories.IPersonalRepository;
import com.grupo10.epicentrogourmet.repositories.IPlatoRepository;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/unidad-venta")
public class UnidadVentaController {

    private final IUnidadVentaService unidadVentaService;
    private final IUsuarioRepository usuarioRepository;
    private final IPlatoRepository platoRepository;
    private final IPersonalRepository personalRepository;
    private final IUnidadVentaRepository unidadVentaRepository;

    public UnidadVentaController(IUnidadVentaService unidadVentaService, IUsuarioRepository usuarioRepository, IPlatoRepository platoRepository,
                                 IPersonalRepository personalRepository, IUnidadVentaRepository unidadVentaRepository) {
        this.unidadVentaService = unidadVentaService;
        this.usuarioRepository = usuarioRepository;
        this.platoRepository = platoRepository;
        this.personalRepository = personalRepository;
        this.unidadVentaRepository = unidadVentaRepository;
    }

    private UnidadVenta getUnidadDelLogueado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UnidadVenta> opt = unidadVentaRepository.findMiUnidad(email);
        if (opt.isPresent()) return opt.get();
        Personal p = personalRepository.findByCredencialesCorreo(email)
                .orElseThrow(() -> new RuntimeException("No existe personal con correo: " + email));
        return p.getUnidadAsignada();
    }

    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN"));
    }

    // SOLO PARA OPERAR: agregar / editar / eliminar -> admin NO puede
    private void validarQueEsMiUnidad(Long idUnidadUrl) {
        if (esAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El administrador no puede operar en una unidad.");
        }
        UnidadVenta miUnidad = getUnidadDelLogueado();
        if (miUnidad == null || !miUnidad.getId().equals(idUnidadUrl)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No podés ver la unidad " + idUnidadUrl + " - vos sos de " + (miUnidad != null ? miUnidad.getNombreComercial() : "sin unidad"));
        }
    }

    // SOLO PARA VER EL CATÁLOGO: admin SÍ puede, solo lectura
    private void validarAccesoCatalogo(Long idUnidadUrl) {
        if (esAdmin()) {
            return; // admin puede ver cualquier catálogo
        }
        UnidadVenta miUnidad = getUnidadDelLogueado();
        if (miUnidad == null || !miUnidad.getId().equals(idUnidadUrl)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No podés ver la unidad " + idUnidadUrl + " - vos sos de " + (miUnidad != null ? miUnidad.getNombreComercial() : "sin unidad"));
        }
    }

    @GetMapping("/new")
    public ModelAndView mostrarFormulario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean logueado = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
        if (logueado) {
            return new ModelAndView("redirect:/index");
        }
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UNIDAD_VENTA_FORM);
        mAV.addObject("unidadDTO", new RequestUnidadVentaDTO());
        return mAV;
    }

    @PostMapping("/save")
    // PUBLICO - sin PreAuthorize, registro de unidad sin loguearse
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
        validarAccesoCatalogo(id);
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
            esCajero = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("CAJERO") || a.getAuthority().contains("COCINERO") || a.getAuthority().contains("EMPLEADO"));
        }
        ModelAndView mav = new ModelAndView("unidad-venta/detalle");
        mav.addObject("unidad", u);
        mav.addObject("platos", platosOrdenados);
        mav.addObject("esResponsable", esResponsable);
        mav.addObject("esAdmin", esAdmin);
        mav.addObject("esCajero", esCajero);
        return mav;
    }

    @GetMapping("/detalle/{id}")
    public ModelAndView detalleAlias(@PathVariable Long id) {
        return new ModelAndView("redirect:/unidad-venta/" + id);
    }

    @PostMapping("/{id}/agregar-plato")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String agregarPlato(@PathVariable Long id, @RequestParam String nombre, @RequestParam double costoProduccion, @RequestParam double precioVenta, @RequestParam(required = false) String imagenUrl,
                               @RequestParam(required = false) Integer stockPlato,
                               RedirectAttributes redirectAttributes) {
        validarQueEsMiUnidad(id);
        if (stockPlato != null && stockPlato < 0) {
            redirectAttributes.addFlashAttribute("platoError", "El stock no puede ser un valor negativo.");
            return "redirect:/unidad-venta/" + id;
        }
        Plato plato = new Plato();
        plato.setNombre(nombre);
        plato.setCostoProduccion(costoProduccion);
        plato.setPrecioVenta(precioVenta);
        plato.setImagenUrl(imagenUrl);
        plato.setStockPlato(stockPlato);
        unidadVentaService.agregarPlato(id, plato);
        return "redirect:/unidad-venta/" + id;
    }

    @PostMapping("/{id}/eliminar-plato/{platoId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String eliminarPlato(@PathVariable Long id, @PathVariable Long platoId) {
        validarQueEsMiUnidad(id);
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
        validarQueEsMiUnidad(plato.getUnidadVenta().getId());
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
                              @RequestParam(value="origen", required=false) String origen,
                              @RequestParam(required = false) Integer stockPlato,
                              RedirectAttributes redirectAttributes) {
        Plato platoActual = platoRepository.findByIdWithUnidad(platoId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado " + platoId));
        validarQueEsMiUnidad(platoActual.getUnidadVenta().getId());
        if (stockPlato != null && stockPlato < 0) {
            Long unidadIdTemp = platoActual.getUnidadVenta().getId();
            redirectAttributes.addFlashAttribute("platoError", "El stock no puede ser un valor negativo.");
            return "redirect:/unidad-venta/" + unidadIdTemp;
        }
        Plato actualizado = new Plato();
        actualizado.setNombre(nombre);
        actualizado.setCostoProduccion(costoProduccion);
        actualizado.setPrecioVenta(precioVenta);
        actualizado.setImagenUrl(imagenUrl);
        actualizado.setStockPlato(stockPlato);
        unidadVentaService.modificarPlato(platoId, actualizado);
        if("mi-unidad".equals(origen)) return "redirect:/mi-unidad?platoEdited=true";
        return "redirect:/unidad-venta/" + platoRepository.findByIdWithUnidad(platoId).get().getUnidadVenta().getId();
    }
}