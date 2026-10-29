package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.dtos.ReporteVentaDTO;
import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.repositories.*;
import com.grupo10.epicentrogourmet.services.IPedidoService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    public PedidoController(IPedidoRepository pedidoRepository, IPlatoRepository platoRepository, IUnidadVentaRepository unidadVentaRepository,
                            IPedidoService pedidoService, IFestivalRepository festivalRepository,
                            IPersonalRepository personalRepository) {
        this.pedidoRepository = pedidoRepository;
        this.platoRepository = platoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.pedidoService = pedidoService;
        this.festivalRepository = festivalRepository;
        this.personalRepository = personalRepository;
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
            uv = unidadVentaRepository.findById(unidadId).orElseThrow();
        } else {
            uv = unidadVentaRepository.findUnidadByEmpleadoCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Cajero sin unidad: " + correo));
        }
        ModelAndView mav = new ModelAndView("pedido/form");
        mav.addObject("unidadVenta", uv);
        mav.addObject("platos", platoRepository.findByUnidadVenta_IdAndActivoTrue(uv.getId()));
        mav.addObject("platoIdPreseleccionado", platoId);
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
        UnidadVenta uv = unidadVentaRepository.findById(unidadVentaId).orElseThrow(() -> new RuntimeException("Unidad no encontrada"));
        ModelAndView mav = new ModelAndView("pedido/form");
        mav.addObject("unidadVenta", uv);
        mav.addObject("platos", platoRepository.findByUnidadVenta_IdAndActivoTrue(unidadVentaId));
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
        mav.addObject("pedidos", pedidos);
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('EMPLEADO','ROLE_EMPLEADO','RESPONSABLE','ROLE_RESPONSABLE')")
    @GetMapping("/unidad/{unidadVentaId}")
    @Transactional(readOnly = true)
    public ModelAndView listarPorUnidad(@PathVariable Long unidadVentaId) {
        UnidadVenta uv = unidadVentaRepository.findById(unidadVentaId).orElseThrow();
        var pedidos = pedidoRepository.findByUnidadVentaIdConDetalles(unidadVentaId);
        ModelAndView mav = new ModelAndView("pedido/lista");
        mav.addObject("unidadVenta", uv);
        mav.addObject("pedidos", pedidos);
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('EMPLEADO','ROLE_EMPLEADO')")
    @PostMapping("/save")
    @Transactional
    public ModelAndView guardarPedido(@RequestParam Long unidadVentaId,
                                      @RequestParam Long platoId,
                                      @RequestParam int cantidad) {
        // VALIDACION CAJERO - FIX PARA QUE COCINERA NO PUEDA
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Personal personal = personalRepository.findByCredencialesCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + correo));
        if(!(personal instanceof Cajero)){
            throw new AccessDeniedException("Solo cajeros pueden dar de alta pedidos - Cocinero no autorizado");
        }

        var plato = platoRepository.findById(platoId).orElseThrow();
        if(!plato.isActivo()){
            throw new RuntimeException("Plato dado de baja lógica");
        }
        UnidadVenta uv = unidadVentaRepository.findById(unidadVentaId).orElseThrow();
        Pedido pedido = new Pedido();
        pedido.setUnidadVenta(uv);
        pedido.setFechaPedido(LocalDate.now());
        DetallePedido detalle = new DetallePedido();
        detalle.setCantidad(cantidad);
        detalle.setPlato(plato);
        detalle.setPedido(pedido);
        pedido.getDetalles().add(detalle);
        pedidoRepository.save(pedido);
        return new ModelAndView("redirect:/pedido/unidad/" + unidadVentaId);
    }

    @PreAuthorize("hasAnyAuthority('RESPONSABLE','ROLE_RESPONSABLE')")
    @GetMapping("/reporte/unidad/{unidadVentaId}")
    public ModelAndView verReporteRecaudacionUnidad(@PathVariable("unidadVentaId") Long unidadVentaId) {
        UnidadVenta unidadVenta = unidadVentaRepository.findById(unidadVentaId)
                .orElseThrow(() -> new RuntimeException("No se encontro la unidad de venta"));
        ReporteVentaDTO reporte = pedidoService.generarReportePorUnidadVenta(unidadVenta);
        ModelAndView mav = new ModelAndView("pedido/reporte-recaudacion");
        mav.addObject("reporte", reporte);
        return mav;
    }
}