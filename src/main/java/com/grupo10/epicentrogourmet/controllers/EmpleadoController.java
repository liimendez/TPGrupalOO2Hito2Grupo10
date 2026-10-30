package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.dtos.PersonalDTO;
import com.grupo10.epicentrogourmet.dtos.RequestPersonalDTO;
import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.helpers.ViewRouteHelper;
import com.grupo10.epicentrogourmet.repositories.ICategoriaCocinaRepository;
import com.grupo10.epicentrogourmet.repositories.ILiquidacionDeHaberRepository;
import com.grupo10.epicentrogourmet.repositories.IPersonalRepository;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;
import com.grupo10.epicentrogourmet.services.ILiquidacionDeHaberService;
import com.grupo10.epicentrogourmet.services.implementation.PersonalService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @Autowired private IPersonalRepository personalRepository;
    @Autowired private IUsuarioRepository usuarioRepository;
    @Autowired private PersonalService personalService;
    @Autowired private ILiquidacionDeHaberRepository liquidacionRepository;
    @Autowired private ILiquidacionDeHaberService liquidacionDeHaberService;
    @Autowired private ICategoriaCocinaRepository categoriaCocinaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public EmpleadoController(IUnidadVentaRepository unidadVentaRepository,
                              IPersonalRepository personalRepository,
                              IUsuarioRepository usuarioRepository,
                              PersonalService personalService,
                              ILiquidacionDeHaberRepository liquidacionRepository,
                              ILiquidacionDeHaberService liquidacionDeHaberService) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.personalRepository = personalRepository;
        this.usuarioRepository = usuarioRepository;
        this.personalService = personalService;
        this.liquidacionRepository = liquidacionRepository;
        this.liquidacionDeHaberService = liquidacionDeHaberService;
    }

    private UnidadVenta getUnidadDelLogueado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UnidadVenta> opt = unidadVentaRepository.findMiUnidad(email);
        if (opt.isPresent()) return opt.get();
        Personal p = personalRepository.findByCredencialesCorreo(email)
                .orElseThrow(() -> new RuntimeException("No existe personal con correo: " + email));
        return p.getUnidadAsignada();
    }

    private void validarQueEsDeMiUnidad(Personal p) {
        UnidadVenta miUnidad = getUnidadDelLogueado();
        if (p.getUnidadAsignada() == null || !p.getUnidadAsignada().getId().equals(miUnidad.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No podés tocar empleados de " + (p.getUnidadAsignada() != null ? p.getUnidadAsignada().getNombreComercial() : "otra unidad") +
                            " - vos sos de " + miUnidad.getNombreComercial());
        }
    }

    @GetMapping({"/lista", "/listaEmpleado"})
    @Transactional
    public String listaEmpleados(@RequestParam(value = "filtro", required = false) String filtro, Model model) {
        UnidadVenta uv = getUnidadDelLogueado();
        var empleados = personalRepository.findByUnidadVentaId(uv.getId());
        if (filtro != null && !filtro.isBlank()) {
            String f = filtro.toLowerCase();
            empleados = empleados.stream()
                    .filter(e ->
                            (e.getNombre() != null && e.getNombre().toLowerCase().contains(f)) ||
                                    (e.getApellido() != null && e.getApellido().toLowerCase().contains(f)) ||
                                    (e.getDni() != null && e.getDni().toLowerCase().contains(f)) ||
                                    (e.getCredenciales() != null && e.getCredenciales().getCorreo() != null
                                            && e.getCredenciales().getCorreo().toLowerCase().contains(f))
                    ).toList();
        }
        model.addAttribute("empleados", empleados);
        model.addAttribute("filtro", filtro);
        model.addAttribute("unidad", uv);
        model.addAttribute("categoriasCocina", categoriaCocinaRepository.findAll());
        if (!model.containsAttribute("nuevoEmpleado")) {
            model.addAttribute("nuevoEmpleado", new RequestPersonalDTO());
        }
        return "empleados/listaEmpleado";
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute("nuevoEmpleado") RequestPersonalDTO req, Model model) {
        UnidadVenta uv = getUnidadDelLogueado();
        try {
            PersonalDTO dto = new PersonalDTO();
            dto.setNombre(req.getNombre());
            dto.setApellido(req.getApellido());
            dto.setDni(req.getDni());
            dto.setEmail(req.getEmail());
            dto.setFechaNacimiento(req.getFechaNacimiento());
            dto.setFechaIngreso(req.getFechaIngreso());
            dto.setTurno(req.getTurno());
            dto.setCategoria(req.getCategoria()); // EJECUTIVO / AYUDANTE / LAVAPLATOS
            personalService.altaConUsuario(req.getTipoPersonal(), dto, uv.getId());
            return "redirect:" + ViewRouteHelper.EMPLEADO_ROUTE;
        } catch (IllegalArgumentException e) {
            model.addAttribute("unidad", uv);
            model.addAttribute("empleados", personalRepository.findByUnidadVentaId(uv.getId()));
            model.addAttribute("nuevoEmpleado", req);
            model.addAttribute("categoriasCocina", categoriaCocinaRepository.findAll());
            model.addAttribute("error", e.getMessage());
            return ViewRouteHelper.EMPLEADO_LISTA;
        }
    }

    @PostMapping("/eliminar/{id}") @Transactional
    public String eliminar(@PathVariable Long id) {
        var p = personalRepository.findByIdIncludingInactive(id).orElseThrow();
        validarQueEsDeMiUnidad(p);
        personalService.remove(id);
        return "redirect:" + ViewRouteHelper.EMPLEADO_ROUTE;
    }

    @PostMapping("/reactivar/{id}") @Transactional
    public String reactivar(@PathVariable Long id) {
        var p = personalRepository.findByIdIncludingInactive(id).orElseThrow();
        validarQueEsDeMiUnidad(p);
        personalService.reactivar(id);
        return "redirect:" + ViewRouteHelper.EMPLEADO_ROUTE;
    }

    @GetMapping("/editar/{id}") @Transactional
    public String editar(@PathVariable Long id, Model model) {
        var p = personalRepository.findByIdIncludingInactive(id).orElseThrow();
        validarQueEsDeMiUnidad(p);
        UnidadVenta uv = getUnidadDelLogueado();
        RequestPersonalDTO req = new RequestPersonalDTO();
        req.setNombre(p.getNombre()); req.setApellido(p.getApellido());
        req.setDni(p.getDni());
        req.setEmail(p.getCredenciales() != null ? p.getCredenciales().getCorreo() : "");
        req.setFechaNacimiento(p.getFechaNacimiento()); req.setFechaIngreso(p.getFechaIngreso());
        if(p instanceof Cajero) req.setTipoPersonal("CAJERO");
        else if(p instanceof Cocinero c) {
            req.setTipoPersonal("COCINERO");
            if(c.getCategoriaCocina() != null) req.setCategoria(c.getCategoriaCocina().getNombre());
        }
        model.addAttribute("empleadoId", id);
        model.addAttribute("nuevoEmpleado", req);
        model.addAttribute("unidad", uv);
        model.addAttribute("personal", p);
        model.addAttribute("categoriasCocina", categoriaCocinaRepository.findAll());
        return ViewRouteHelper.EMPLEADO_EDITAR;
    }

    @PostMapping("/editar/{id}")
    @Transactional
    public String guardarEdicion(@PathVariable Long id, @ModelAttribute("nuevoEmpleado") RequestPersonalDTO req) {
        var p = personalRepository.findByIdIncludingInactive(id).orElseThrow();
        validarQueEsDeMiUnidad(p);

        String tipoNuevo = req.getTipoPersonal();
        String tipoViejo = p instanceof Cajero ? "CAJERO" : "COCINERO";

        if (!tipoViejo.equals(tipoNuevo)) {
            if (p instanceof Cocinero) {
                entityManager.createNativeQuery("DELETE FROM cocinero WHERE id = :id").setParameter("id", id).executeUpdate();
            } else if (p instanceof Cajero) {
                entityManager.createNativeQuery("DELETE FROM cajero WHERE id = :id").setParameter("id", id).executeUpdate();
            }
            if("CAJERO".equals(tipoNuevo)) {
                entityManager.createNativeQuery("INSERT INTO cajero (id, turno) VALUES (:id, 'MANIANA')").setParameter("id", id).executeUpdate();
            } else {
                Long catId = 3L;
                if(req.getCategoria() != null){
                    var cat = categoriaCocinaRepository.findByNombreIgnoreCase(req.getCategoria()).orElse(null);
                    if(cat != null) catId = cat.getId();
                }
                entityManager.createNativeQuery("INSERT INTO cocinero (id, categoria_id) VALUES (:id, :catId)")
                        .setParameter("id", id).setParameter("catId", catId).executeUpdate();
            }
        } else if(p instanceof Cocinero coc && req.getCategoria() != null){
            var cat = categoriaCocinaRepository.findByNombreIgnoreCase(req.getCategoria()).orElse(null);
            if(cat != null){
                coc.setCategoriaCocina(cat);
                personalRepository.save(p);
            }
        }

        p.setNombre(req.getNombre()); p.setApellido(req.getApellido()); p.setDni(req.getDni());
        p.setFechaNacimiento(req.getFechaNacimiento()); p.setFechaIngreso(req.getFechaIngreso());
        personalRepository.save(p);
        if (p.getCredenciales() != null) {
            p.getCredenciales().setCorreo(req.getEmail());
            usuarioRepository.save(p.getCredenciales());
        }
        return "redirect:" + ViewRouteHelper.EMPLEADO_ROUTE;
    }

    @GetMapping("/liquidar/{id}") @Transactional
    public String liquidar(@PathVariable Long id, Model model) {
        var p = personalRepository.findByIdIncludingInactive(id).orElseThrow();
        validarQueEsDeMiUnidad(p);
        var uv = p.getUnidadAsignada();
        if(uv == null) throw new RuntimeException("Empleado " + id + " sin unidad");
        var hoy = LocalDate.now();
        if(liquidacionRepository.existsByPersonal_IdAndFechaLiquidacion(id, LocalDate.now())) {
            return "redirect:/empleado/liquidaciones/" + id;
        }
        var liq = liquidacionDeHaberService.liquidar(p.getId(), uv.getId());
        model.addAttribute("personal", p);
        model.addAttribute("unidad", uv);
        model.addAttribute("sueldo", BigDecimal.valueOf(liq.getTotalACobrar()));
        model.addAttribute("anios", personalService.getAntiguedadAnios(id));
        model.addAttribute("liquidacion", liq);
        return ViewRouteHelper.EMPLEADO_LIQUIDACION;
    }

    @GetMapping("/liquidaciones/{id}") @Transactional(readOnly = true)
    public String historial(@PathVariable Long id, Model model) {
        var p = personalRepository.findByIdIncludingInactive(id).orElseThrow();
        validarQueEsDeMiUnidad(p);
        model.addAttribute("personal", p);
        model.addAttribute("liquidaciones", liquidacionRepository.findByPersonalIdOrderByFechaLiquidacionDesc(id));
        return ViewRouteHelper.EMPLEADO_HISTORIAL;
    }

    @GetMapping("/liquidacion/ver/{liquidacionId}") @Transactional(readOnly = true)
    public String verLiquidacion(@PathVariable Long liquidacionId, Model model) {
        var liq = liquidacionRepository.findById(liquidacionId).orElseThrow(() -> new RuntimeException("Liquidación no encontrada"));
        if(liq.getPersonal() == null) throw new RuntimeException("Liquidación vieja corrupta, borrar id " + liquidacionId);
        var p = liq.getPersonal();
        validarQueEsDeMiUnidad(p);
        model.addAttribute("personal", p);
        model.addAttribute("unidad", liq.getUnidadVenta());
        model.addAttribute("sueldo", BigDecimal.valueOf(liq.getTotalACobrar()));
        model.addAttribute("anios", personalService.getAntiguedadAnios(p.getId()));
        model.addAttribute("liquidacion", liq);
        return ViewRouteHelper.EMPLEADO_LIQUIDACION;
    }

    @GetMapping("/mi-informacion") @PreAuthorize("hasRole('EMPLEADO')") @Transactional(readOnly = true)
    public ModelAndView verMiInformacionPropia() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_MI_INFORMACION);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : "";
        Personal empleado = personalRepository.findByCredencialesCorreo(email)
                .orElseThrow(() -> new RuntimeException("No se encontró el Usuario del empleado para el correo: " + email));
        UnidadVenta unidadActual = empleado.getUnidadAsignada();
        var liquidaciones = liquidacionRepository.findByPersonalIdOrderByFechaLiquidacionDesc(empleado.getId());
        Set<UnidadVenta> unidadesHistoricas = liquidaciones.stream()
                .map(LiquidacionDeHaber::getUnidadVenta)
                .filter(uv -> uv != null)
                .collect(Collectors.toSet());
        mAV.addObject("empleado", empleado);
        mAV.addObject("unidadActual", unidadActual);
        mAV.addObject("festivalesActuales", unidadActual != null ? unidadActual.getFestivales() : null);
        mAV.addObject("unidadesHistoricas", unidadesHistoricas);
        mAV.addObject("liquidaciones", liquidaciones);
        return mAV;
    }

    @GetMapping("/mi-informacion/recibo/{liquidacionId}")
    @PreAuthorize("hasRole('EMPLEADO')")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> descargarReciboSueldo(@PathVariable Long liquidacionId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Personal empleado = personalRepository.findByCredencialesCorreo(email)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        LiquidacionDeHaber liq = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new RuntimeException("Liquidación no encontrada"));

        if (!liq.getPersonal().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No podés descargar recibos de otros empleados");
        }

        byte[] pdf = liquidacionDeHaberService.generarReciboSueldoPdf(liquidacionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Recibo_Sueldo_" + liquidacionId + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}