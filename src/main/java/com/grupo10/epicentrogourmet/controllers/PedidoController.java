package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.dtos.ReporteVentaDTO;
import com.grupo10.epicentrogourmet.entities.DetallePedido;
import com.grupo10.epicentrogourmet.entities.Pedido;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IFestivalRepository;
import com.grupo10.epicentrogourmet.repositories.IPedidoRepository;
import com.grupo10.epicentrogourmet.repositories.IPlatoRepository;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.services.IPedidoService;

import org.springframework.security.access.prepost.PreAuthorize;
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

    public PedidoController(IPedidoRepository pedidoRepository, IPlatoRepository platoRepository, IUnidadVentaRepository unidadVentaRepository,
                            IPedidoService pedidoService, IFestivalRepository festivalRepository) {
        this.pedidoRepository = pedidoRepository;
        this.platoRepository = platoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.pedidoService = pedidoService;
        this.festivalRepository = festivalRepository;
    }

    @GetMapping("/new/{unidadVentaId}")
    public ModelAndView nuevoPedido(@PathVariable Long unidadVentaId) {
        ModelAndView mav = new ModelAndView("pedido/form");
        UnidadVenta uv = unidadVentaRepository.findById(unidadVentaId).orElseThrow(() -> new RuntimeException("Unidad no encontrada"));
        mav.addObject("unidadVenta", uv);
        mav.addObject("platos", platoRepository.findByUnidadVenta_Id(unidadVentaId));
        return mav;
    }

    @PostMapping("/save")
    @Transactional
    public ModelAndView guardarPedido(@RequestParam Long unidadVentaId,
                                      @RequestParam Long platoId,
                                      @RequestParam int cantidad) {
        UnidadVenta uv = unidadVentaRepository.findById(unidadVentaId).orElseThrow();
        var plato = platoRepository.findById(platoId).orElseThrow();

        Pedido pedido = new Pedido();
        pedido.setUnidadVenta(uv);
        pedido.setFechaPedido(LocalDate.now());

        DetallePedido detalle = new DetallePedido();
        detalle.setCantidad(cantidad);
        detalle.setPlato(plato);
        detalle.setPedido(pedido);

        pedido.getDetalles().add(detalle);

        pedidoRepository.save(pedido);

        return new ModelAndView("redirect:/festival/1/ranking");
    }

    @PreAuthorize("hasRole('RESPONSABLE')")
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