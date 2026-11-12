package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.dtos.CierreCajaDTO;
import com.grupo10.epicentrogourmet.dtos.ReporteVentaDTO;
import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.repositories.*;
import com.grupo10.epicentrogourmet.services.IFestivalService;
import com.grupo10.epicentrogourmet.services.IPedidoService;
import com.grupo10.epicentrogourmet.repositories.IMetodoPagoRepository;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    private final IPedidoRepository pedidoRepository;
    private final IPlatoRepository platoRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final IPedidoService pedidoService;
    private final IFestivalRepository festivalRepository;
    private final IPersonalRepository personalRepository;
    private final IFestivalService festivalService;
    private final IMetodoPagoRepository metodoPagoRepository;

    public PedidoController(IPedidoRepository pedidoRepository, IPlatoRepository platoRepository, IUnidadVentaRepository unidadVentaRepository,
                            IPedidoService pedidoService, IFestivalRepository festivalRepository,
                            IPersonalRepository personalRepository, IFestivalService festivalService, IMetodoPagoRepository metodoPagoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.platoRepository = platoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.pedidoService = pedidoService;
        this.festivalRepository = festivalRepository;
        this.personalRepository = personalRepository;
        this.festivalService = festivalService;
        this.metodoPagoRepository = metodoPagoRepository;
    }

    // === FIX SEGURIDAD: nadie ve / vende en una unidad que no es suya ===
    private UnidadVenta getUnidadDelLogueado() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UnidadVenta> opt = unidadVentaRepository.findMiUnidad(correo);
        if (opt.isPresent()) return opt.get();
        Personal p = personalRepository.findByCredencialesCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No existe personal con correo: " + correo));
        return p.getUnidadAsignada();
    }

    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN"));
    }

    private void validarQueEsMiUnidad(Long idUnidadUrl) {
        if (esAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El administrador no puede ver pedidos de una unidad.");
        }
        UnidadVenta miUnidad = getUnidadDelLogueado();
        if (miUnidad == null || !miUnidad.getId().equals(idUnidadUrl)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No podés ver pedidos de la unidad " + idUnidadUrl + " - vos sos de " + (miUnidad != null ? miUnidad.getNombreComercial() : "sin unidad"));
        }
    }

    private void validarPlatoEsDeMiUnidad(Plato plato, Long idUnidadUrl) {
        if (plato.getUnidadVenta() == null || !plato.getUnidadVenta().getId().equals(idUnidadUrl)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ese plato no es de tu unidad.");
        }
    }

    @PreAuthorize("hasAnyAuthority('EMPLEADO','ROLE_EMPLEADO')")
    @GetMapping("/new")
    public ModelAndView nuevoPedidoCajero(@RequestParam(required = false) Long unidadId,
                                          @RequestParam(required = false) Long platoId) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Personal personal = personalRepository.findByCredencialesCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + correo));
        if(!(personal instanceof Cajero)){
            throw new AccessDeniedException("Solo cajeros pueden dar de alta pedidos");
        }
        UnidadVenta uv;
        if(unidadId != null){
            validarQueEsMiUnidad(unidadId);
            uv = unidadVentaRepository.findById(unidadId).orElseThrow();
        } else {
            uv = unidadVentaRepository.findUnidadByEmpleadoCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Cajero sin unidad: " + correo));
        }
        ModelAndView mav = new ModelAndView("pedido/form");
        mav.addObject("unidadVenta", uv);
        mav.addObject("platos", platoRepository.findByUnidadVenta_IdAndActivoTrue(uv.getId()));
        mav.addObject("platoIdPreseleccionado", platoId);
        mav.addObject("metodosPago", metodoPagoRepository.findByActivoTrue());
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('EMPLEADO','ROLE_EMPLEADO')")
    @GetMapping("/new/{unidadVentaId}")
    public ModelAndView nuevoPedido(@PathVariable Long unidadVentaId) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Personal personal = personalRepository.findByCredencialesCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        if(!(personal instanceof Cajero)){
            throw new AccessDeniedException("Solo cajeros pueden dar de alta pedidos");
        }
        validarQueEsMiUnidad(unidadVentaId);
        UnidadVenta uv = unidadVentaRepository.findById(unidadVentaId).orElseThrow(() -> new RuntimeException("Unidad no encontrada"));
        ModelAndView mav = new ModelAndView("pedido/form");
        mav.addObject("unidadVenta", uv);
        mav.addObject("platos", platoRepository.findByUnidadVenta_IdAndActivoTrue(unidadVentaId));
        mav.addObject("metodosPago", metodoPagoRepository.findByActivoTrue());
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('EMPLEADO','ROLE_EMPLEADO','RESPONSABLE','ROLE_RESPONSABLE')")
    @GetMapping("/lista")
    @Transactional(readOnly = true)
    public ModelAndView listarPedidosDeMiUnidad() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        UnidadVenta uv = unidadVentaRepository.findUnidadByEmpleadoCorreo(correo)
                .orElseGet(() -> unidadVentaRepository.findMiUnidad(correo)
                        .orElseThrow(() -> new RuntimeException("Sin unidad: " + correo)));
        var pedidos = pedidoRepository.findByUnidadVentaIdConDetalles(uv.getId());
        ModelAndView mav = new ModelAndView("pedido/lista");
        mav.addObject("unidadVenta", uv);
        mav.addObject("tickets", pedidoService.toDTOList(pedidos));
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('EMPLEADO','ROLE_EMPLEADO','RESPONSABLE','ROLE_RESPONSABLE')")
    @GetMapping("/unidad/{unidadVentaId}")
    @Transactional(readOnly = true)
    public ModelAndView listarPorUnidad(@PathVariable Long unidadVentaId) {
        validarQueEsMiUnidad(unidadVentaId);
        UnidadVenta uv = unidadVentaRepository.findById(unidadVentaId).orElseThrow();
        var pedidos = pedidoRepository.findByUnidadVentaIdConDetalles(unidadVentaId);
        ModelAndView mav = new ModelAndView("pedido/lista");
        mav.addObject("unidadVenta", uv);
        mav.addObject("tickets", pedidoService.toDTOList(pedidos));
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('EMPLEADO','ROLE_EMPLEADO')")
    @PostMapping("/save")
    public ModelAndView guardarPedido(@RequestParam Long unidadVentaId,
                                      @RequestParam Long platoId,
                                      @RequestParam int cantidad,
                                      @RequestParam(required = false) Long metodoPagoId,
                                      RedirectAttributes redirectAttributes) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Personal personal = personalRepository.findByCredencialesCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + correo));
        if(!(personal instanceof Cajero)){
            throw new AccessDeniedException("Solo cajeros pueden dar de alta pedidos - Cocinero no autorizado");
        }
        validarQueEsMiUnidad(unidadVentaId);
        var plato = platoRepository.findById(platoId).orElseThrow();
        validarPlatoEsDeMiUnidad(plato, unidadVentaId);
        if(!plato.isActivo()){
            throw new RuntimeException("Plato dado de baja lógica");
        }
        UnidadVenta uv = unidadVentaRepository.findById(unidadVentaId).orElseThrow();
        Pedido pedido = new Pedido();
        pedido.setUnidadVenta(uv);
        pedido.setFechaPedido(LocalDate.now());
        if (metodoPagoId != null) {
            MetodoPago mp = metodoPagoRepository.findById(metodoPagoId).orElse(null);
            pedido.setMetodoPago(mp);
        }
        DetallePedido detalle = new DetallePedido();
        detalle.setCantidad(cantidad);
        detalle.setPlato(plato);
        detalle.setSubtotal(cantidad * plato.getPrecioVenta());
        detalle.setPedido(pedido);
        pedido.getDetalles().add(detalle);
        try {
            pedidoService.insertOrUpdate(pedido);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return new ModelAndView("redirect:/unidad-venta/" + unidadVentaId);
        }
        return new ModelAndView("redirect:/pedido/unidad/" + unidadVentaId);
    }

    @PreAuthorize("hasAnyAuthority('RESPONSABLE','ROLE_RESPONSABLE')")
    @GetMapping("/reporte/unidad/{unidadVentaId}")
    @Transactional(readOnly = true)
    public ModelAndView verReporteRecaudacionUnidad(@PathVariable("unidadVentaId") Long unidadVentaId) {
        validarQueEsMiUnidad(unidadVentaId);
        UnidadVenta unidadVenta = unidadVentaRepository.findById(unidadVentaId)
                .orElseThrow(() -> new RuntimeException("No se encontro la unidad de venta"));
        LocalDate hoy = LocalDate.now();
        Festival festivalActivo = festivalRepository.findAll().stream()
                .filter(f -> f.getFechaInicio() != null && f.getFechaFin() != null)
                .filter(f -> !hoy.isBefore(f.getFechaInicio()) && !hoy.isAfter(f.getFechaFin()))
                .findFirst()
                .orElse(festivalRepository.findAll().stream()
                        .filter(f -> f.getFechaFin() != null)
                        .max((a,b) -> a.getFechaFin().compareTo(b.getFechaFin()))
                        .orElse(null));
        var todosLosPedidos = pedidoRepository.findByUnidadVentaIdConDetalles(unidadVentaId);
        var pedidosFiltrados = todosLosPedidos;
        if (festivalActivo != null) {
            pedidosFiltrados = todosLosPedidos.stream()
                    .filter(p -> p.getFechaPedido() != null)
                    .filter(p -> !p.getFechaPedido().isBefore(festivalActivo.getFechaInicio())
                            && !p.getFechaPedido().isAfter(festivalActivo.getFechaFin()))
                    .toList();
        }
        double totalFestival = pedidosFiltrados.stream().mapToDouble(p -> p.getTotal()).sum();
        double totalHistorico = todosLosPedidos.stream().mapToDouble(p -> p.getTotal()).sum();
        ReporteVentaDTO reporte = new ReporteVentaDTO();
        if (festivalActivo != null) {
            reporte.setFestivalId(festivalActivo.getId());
            reporte.setNombreFestival(festivalActivo.getNombre());
        }
        reporte.setRecaudacionTotal(totalFestival);
        reporte.setCantidadPedidos(pedidosFiltrados.size());
        ModelAndView mav = new ModelAndView("pedido/reporte-recaudacion");
        mav.addObject("reporte", reporte);
        mav.addObject("unidadVenta", unidadVenta);
        mav.addObject("festivalActivo", festivalActivo);
        mav.addObject("pedidosFiltrados", pedidosFiltrados);
        mav.addObject("totalFestival", totalFestival);
        mav.addObject("totalHistorico", totalHistorico);
        mav.addObject("cantidadFestival", pedidosFiltrados.size());
        mav.addObject("cantidadHistorico", todosLosPedidos.size());
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('EMPLEADO','ROLE_EMPLEADO')")
    @PostMapping("/saveMulti")
    public String saveMulti(@RequestParam Long unidadVentaId, @RequestParam(required = false) Long metodoPagoId, @RequestParam Map<String,String> allParams, RedirectAttributes redirectAttributes) {
        // TUYO - seguridad
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Personal personal = personalRepository.findByCredencialesCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + correo));
        if(!(personal instanceof Cajero)){
            throw new AccessDeniedException("Solo cajeros pueden dar de alta pedidos - Cocinero no autorizado");
        }
        validarQueEsMiUnidad(unidadVentaId);
        // DE SANTIAGO - metodo de pago
        UnidadVenta unidad = unidadVentaRepository.findById(unidadVentaId).orElseThrow();
        Pedido pedido = new Pedido();
        pedido.setUnidadVenta(unidad);
        pedido.setFechaPedido(LocalDate.now());
        if (metodoPagoId != null) {
            MetodoPago mp = metodoPagoRepository.findById(metodoPagoId).orElse(null);
            pedido.setMetodoPago(mp);
        }
        int agregados = 0;
        for (String key : allParams.keySet()) {
            if (!key.startsWith("cant_")) continue;
            int cant = 0;
            try { cant = Integer.parseInt(allParams.get(key)); } catch(Exception e){ continue; }
            if (cant <= 0) continue;
            Long platoId = Long.parseLong(key.substring(5));
            Plato plato = platoRepository.findById(platoId).orElseThrow();
            validarPlatoEsDeMiUnidad(plato, unidadVentaId);
            DetallePedido det = new DetallePedido();
            det.setPlato(plato);
            det.setCantidad(cant);
            det.setSubtotal(cant * plato.getPrecioVenta());
            det.setPedido(pedido);
            pedido.getDetalles().add(det);
            agregados++;
        }
        if (agregados == 0) {
            return "redirect:/pedido/new?unidadId=" + unidadVentaId + "&error=vacio";
        }
        try {
            pedidoService.insertOrUpdate(pedido);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/unidad-venta/" + unidadVentaId;
        }
        return "redirect:/pedido/unidad/" + unidadVentaId + "?success";
    }

    @GetMapping("/cajero/cierre-caja")
    @PreAuthorize("hasAnyAuthority('EMPLEADO', 'ROLE_EMPLEADO')")
    @Transactional(readOnly = true)
    public ModelAndView verCierreCaja() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        ModelAndView mav = new ModelAndView("pedido/cierre-caja");
        try {
            CierreCajaDTO cierre = festivalService.getCierreCajaDiario(correo);
            mav.addObject("cierre", cierre);
        } catch (RuntimeException e) {
            mav.addObject("error", e.getMessage());
        }
        return mav;
    }
}