package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.Festival;
import com.grupo10.epicentrogourmet.entities.Pedido;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IFestivalRepository;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;
import com.grupo10.epicentrogourmet.repositories.IPedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminDashboardController {

    private final IFestivalRepository festivalRepo;
    private final IUnidadVentaRepository unidadRepo;
    private final IUsuarioRepository userRepo;
    private final IPedidoRepository pedidoRepo;

    public AdminDashboardController(IFestivalRepository festivalRepo,
                                    IUnidadVentaRepository unidadRepo,
                                    IUsuarioRepository userRepo,
                                    IPedidoRepository pedidoRepo) {
        this.festivalRepo = festivalRepo;
        this.unidadRepo = unidadRepo;
        this.userRepo = userRepo;
        this.pedidoRepo = pedidoRepo;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        LocalDate hoy = LocalDate.now();

        long totalFest = festivalRepo.count();
        long activos = festivalRepo.countByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(hoy, hoy);
        var proximosFestivales = festivalRepo.findByFechaFinGreaterThanEqualOrderByFechaInicioAsc(hoy);
        var todosFestivales = festivalRepo.findAllByOrderByFechaInicioAsc();

        List<Pedido> pedidos = pedidoRepo.findAll();

        // Festival ACTIVO actual
        Festival festivalActivo = proximosFestivales.stream()
                .filter(f -> f.getFechaInicio() != null && f.getFechaFin() != null)
                .filter(f -> !hoy.isBefore(f.getFechaInicio()) && !hoy.isAfter(f.getFechaFin()))
                .findFirst()
                .orElse(proximosFestivales.isEmpty() ? null : proximosFestivales.get(0));

        List<Pedido> pedidosFestivalActual = new ArrayList<>();
        if (festivalActivo != null) {
            Set<Long> unidadesActivas = festivalActivo.getUnidadesVenta().stream()
                    .map(UnidadVenta::getId)
                    .collect(Collectors.toSet());
            pedidosFestivalActual = pedidos.stream()
                    .filter(p -> p.getFechaPedido() != null)
                    .filter(p -> !p.getFechaPedido().isBefore(festivalActivo.getFechaInicio()) && !p.getFechaPedido().isAfter(festivalActivo.getFechaFin()))
                    .filter(p -> p.getUnidadVenta() != null && unidadesActivas.contains(p.getUnidadVenta().getId()))
                    .toList();
        }

        // TOP 3 y Recaudación SOLO del festival actual (no histórico)
        Map<String, Double> recaudacionPorUnidad = pedidosFestivalActual.stream()
                .filter(p -> p.getUnidadVenta() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getUnidadVenta().getNombreComercial(),
                        Collectors.summingDouble(Pedido::getTotal)
                ));

        // Ranking y recaudación POR FESTIVAL (para las cards de abajo, histórico completo)
        Map<Festival, Map<String, Double>> rankingPorFestival = new LinkedHashMap<>();
        Map<Festival, Double> totalPorFestival = new LinkedHashMap<>();

        for (Festival fest : todosFestivales) {
            if (fest.getFechaInicio() == null || fest.getFechaFin() == null) continue;
            Set<Long> unidadesDelFestival = fest.getUnidadesVenta().stream()
                    .map(UnidadVenta::getId)
                    .collect(Collectors.toSet());
            if (unidadesDelFestival.isEmpty()) continue;
            List<Pedido> pedidosDelFestival = pedidos.stream()
                    .filter(p -> p.getFechaPedido() != null)
                    .filter(p -> !p.getFechaPedido().isBefore(fest.getFechaInicio()) && !p.getFechaPedido().isAfter(fest.getFechaFin()))
                    .filter(p -> p.getUnidadVenta() != null && unidadesDelFestival.contains(p.getUnidadVenta().getId()))
                    .toList();
            if (pedidosDelFestival.isEmpty()) continue;
            Map<String, Double> porUnidad = pedidosDelFestival.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getUnidadVenta().getNombreComercial(),
                            Collectors.summingDouble(Pedido::getTotal)
                    ));
            double totalFestival = pedidosDelFestival.stream().mapToDouble(Pedido::getTotal).sum();
            rankingPorFestival.put(fest, porUnidad);
            totalPorFestival.put(fest, totalFestival);
        }

        var topUnidades = recaudacionPorUnidad.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .toList();

        double totalRecaudadoActual = pedidosFestivalActual.stream().mapToDouble(Pedido::getTotal).sum();

        model.addAttribute("festivalesActivos", activos);
        model.addAttribute("totalFestivales", totalFest);
        model.addAttribute("totalUnidades", unidadRepo.count());
        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("recaudacionUnidad", recaudacionPorUnidad);
        model.addAttribute("topUnidades", topUnidades);
        model.addAttribute("totalRecaudado", totalRecaudadoActual);
        model.addAttribute("festivales", proximosFestivales);
        model.addAttribute("todosFestivales", todosFestivales);
        model.addAttribute("rankingPorFestival", rankingPorFestival);
        model.addAttribute("totalPorFestival", totalPorFestival);
        model.addAttribute("pedidosCount", pedidosFestivalActual.size());
        model.addAttribute("festivalActivoNombre", festivalActivo != null ? festivalActivo.getNombre() : "Sin festival activo");

        return "admin/dashboard";
    }
}