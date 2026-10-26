package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.dtos.RequestFestivalDTO;
import com.grupo10.epicentrogourmet.entities.Festival;
import com.grupo10.epicentrogourmet.entities.Pedido;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.helpers.ViewRouteHelper;
import com.grupo10.epicentrogourmet.repositories.IPedidoRepository;
import com.grupo10.epicentrogourmet.services.IFestivalService;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/festival")
public class FestivalController {

    private final IFestivalService festivalService;
    private final IUnidadVentaService unidadVentaService;
    private final IPedidoRepository pedidoRepository;

    public FestivalController(IFestivalService festivalService,
                              IUnidadVentaService unidadVentaService,
                              IPedidoRepository pedidoRepository) {
        this.festivalService = festivalService;
        this.unidadVentaService = unidadVentaService;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView nuevoFestival() {
        ModelAndView mav = new ModelAndView(ViewRouteHelper.FESTIVAL_FORM);
        mav.addObject("festivalDTO", new RequestFestivalDTO());
        mav.addObject("esEdicion", false);
        return mav;
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView editar(@PathVariable Long id) {
        Festival f = festivalService.findById(id).orElseThrow(() -> new RuntimeException("Festival no encontrado"));
        RequestFestivalDTO dto = new RequestFestivalDTO();
        dto.setNombre(f.getNombre());
        dto.setTemporada(f.getTemporada());
        dto.setFechaInicio(f.getFechaInicio());
        dto.setFechaFin(f.getFechaFin());
        dto.setImagenUrl(f.getImagenUrl());

        ModelAndView mav = new ModelAndView(ViewRouteHelper.FESTIVAL_FORM);
        mav.addObject("festivalDTO", dto);
        mav.addObject("festivalId", id);
        mav.addObject("esEdicion", true);
        return mav;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView guardarFestival(@ModelAttribute("festivalDTO") RequestFestivalDTO dto) {
        Festival festival = new Festival();
        festival.setNombre(dto.getNombre());
        festival.setTemporada(dto.getTemporada());
        festival.setFechaInicio(dto.getFechaInicio());
        festival.setFechaFin(dto.getFechaFin());
        festival.setImagenUrl(dto.getImagenUrl());
        festivalService.insertOrUpdate(festival);
        return new ModelAndView("redirect:/index?festivalSuccess=true");
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView guardarEdicion(@PathVariable Long id, @ModelAttribute("festivalDTO") RequestFestivalDTO dto) {
        Festival festival = festivalService.findById(id).orElseThrow();
        festival.setNombre(dto.getNombre());
        festival.setTemporada(dto.getTemporada());
        festival.setFechaInicio(dto.getFechaInicio());
        festival.setFechaFin(dto.getFechaFin());
        festival.setImagenUrl(dto.getImagenUrl());
        festivalService.insertOrUpdate(festival);
        return new ModelAndView("redirect:/festival/" + id);
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView eliminar(@PathVariable Long id) {
        festivalService.remove(id);
        return new ModelAndView("redirect:/index?deleted=true");
    }

    @GetMapping("/{id}")
    public ModelAndView verFestival(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        ModelAndView mav = new ModelAndView("festival/detalle");
        Festival festival = festivalService.findByIdWithUnidades(id).orElseThrow(() -> new RuntimeException("Festival no encontrado"));
        mav.addObject("festival", festival);

        boolean esResponsable = user != null && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("RESPONSABLE"));
        boolean esAdmin = user != null && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN"));

        mav.addObject("esResponsable", esResponsable);
        mav.addObject("esAdmin", esAdmin);

        if (esResponsable) {
            try {
                UnidadVenta miUnidad = unidadVentaService.findByCorreoResponsable(user.getUsername());
                mav.addObject("miUnidad", miUnidad);
                boolean yaAnotado = festival.getUnidadesVenta().stream()
                        .anyMatch(uv -> uv.getId().equals(miUnidad.getId()));
                mav.addObject("yaAnotado", yaAnotado);
                mav.addObject("unidadesParaMostrar", yaAnotado ? List.of(miUnidad) : List.of());
            } catch (RuntimeException e) {
                mav.addObject("miUnidad", null);
                mav.addObject("yaAnotado", false);
                mav.addObject("unidadesParaMostrar", List.of());
            }
        } else {
            // ADMIN
            mav.addObject("unidadesParaMostrar", festival.getUnidadesVenta());
            mav.addObject("yaAnotado", false);
            mav.addObject("miUnidad", null);
        }
        mav.addObject("todasUnidades", unidadVentaService.getAll());
        return mav;
    }

    @PostMapping("/{festivalId}/agregar-unidad")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ModelAndView agregarUnidad(@PathVariable Long festivalId, @RequestParam Long unidadVentaId) {
        try {
            festivalService.agregarUnidadAFestival(festivalId, unidadVentaId);
            return new ModelAndView("redirect:/festival/" + festivalId + "?unidadAgregada=true");
        } catch (IllegalArgumentException e) {
            String mensaje = e.getMessage();
            if (mensaje.contains("DUPLICADO")) {
                return new ModelAndView("redirect:/festival/" + festivalId + "?errorDuplicado=true");
            } else if (mensaje.contains("SOLAPAMIENTO")) {
                return new ModelAndView("redirect:/festival/" + festivalId + "?errorSolapamiento=true&mensaje=" + java.net.URLEncoder.encode(mensaje, java.nio.charset.StandardCharsets.UTF_8));
            }
            return new ModelAndView("redirect:/festival/" + festivalId + "?error=true");
        }
    }

    @GetMapping("/{id}/mayor-canon")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ModelAndView mayorCanon(@PathVariable Long id, @RequestParam(defaultValue = "3") int x) {
        ModelAndView mav = new ModelAndView(ViewRouteHelper.FESTIVAL_MAYOR_CANON);
        mav.addObject("festival", festivalService.findById(id).orElse(null));
        mav.addObject("reporte", festivalService.getUnidadesConMayorCanon(id, x));
        mav.addObject("x", x);
        return mav;
    }

    @GetMapping("/{id}/ranking")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public String rankingRecaudacion(@PathVariable Long id, Model model) {
        Festival festival = festivalService.findById(id).orElseThrow(() -> new RuntimeException("Festival no encontrado"));
        List<Pedido> pedidos = pedidoRepository.findByFestivalIdConDetalles(id);

        Map<Long, List<Pedido>> porUnidadId = pedidos.stream()
                .collect(Collectors.groupingBy(p -> p.getUnidadVenta().getId()));

        List<Map<String, Object>> ranking = new ArrayList<>();

        for (UnidadVenta uv : festival.getUnidadesVenta()) {
            List<Pedido> pedidosDeUnidad = porUnidadId.getOrDefault(uv.getId(), List.of());
            double recaudacion = pedidosDeUnidad.stream()
                    .filter(p -> p.getDetalles() != null)
                    .flatMap(p -> p.getDetalles().stream())
                    .filter(d -> d.getPlato() != null)
                    .mapToDouble(d -> d.getCantidad() * d.getPlato().getPrecioVenta())
                    .sum();

            Map<String, Object> fila = new HashMap<>();
            fila.put("nombre", uv.getNombreComercial() + " - " + festival.getNombre());
            fila.put("recaudacion", recaudacion);
            fila.put("pedidos", pedidosDeUnidad.size());
            ranking.add(fila);
        }

        ranking.sort((a, b) -> Double.compare((Double) b.get("recaudacion"), (Double) a.get("recaudacion")));

        model.addAttribute("festival", festival);
        model.addAttribute("ranking", ranking);
        return "festival/ranking";
    }
}