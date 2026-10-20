package com.grupo10.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.grupo10.epicentrogourmet.dtos.PlatoDTO;
import com.grupo10.epicentrogourmet.dtos.ReporteMayoresCanonDTO;
import com.grupo10.epicentrogourmet.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo10.epicentrogourmet.dtos.ReporteVentaDTO;
import com.grupo10.epicentrogourmet.dtos.RentabilidadNetaDTO;
import com.grupo10.epicentrogourmet.repositories.IFestivalRepository;
import com.grupo10.epicentrogourmet.repositories.ILiquidacionDeHaberRepository;
import com.grupo10.epicentrogourmet.repositories.IPedidoRepository;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.services.IFestivalService;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;

@Service("festivalService")
public class FestivalService implements IFestivalService {

    @Autowired private IFestivalRepository festivalRepository;
    @Autowired private IPedidoRepository pedidoRepository;
    @Autowired private ILiquidacionDeHaberRepository liquidacionRepository;
    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @Autowired private IUnidadVentaService unidadVentaService;

    @Override public List<Festival> getAll() { return festivalRepository.findAll(); }
    @Override public Optional<Festival> findById(Long id) { return festivalRepository.findById(id); }
    @Override public Festival insertOrUpdate(Festival festival) { return festivalRepository.save(festival); }
    @Override public boolean remove(Long id) { if(festivalRepository.existsById(id)){ festivalRepository.deleteById(id); return true;} return false; }

    // PUNTO 5
    @Override
    public ReporteVentaDTO getRecaudacionTotal(Long festivalId) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new RuntimeException("Festival no encontrado"));
        List<Pedido> pedidos = pedidoRepository.findByFestivalId(festivalId);
        double total = pedidos.stream()
                .mapToDouble(p -> p.getDetalles() == null ? 0 : p.getDetalles().stream()
                        .mapToDouble(d -> d.getCantidad() * d.getPlato().getPrecioVenta()).sum())
                .sum();
        return new ReporteVentaDTO(festival.getId(), festival.getNombre(), total, pedidos.size());
    }

    // PUNTO 6
    @Override
    public RentabilidadNetaDTO getRentabilidadNeta(Long festivalId, Long unidadVentaId) {
        UnidadVenta unidad = unidadVentaRepository.findById(unidadVentaId)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

        List<Pedido> pedidos = pedidoRepository.findByFestivalIdAndUnidadVentaId(festivalId, unidadVentaId);

        double gananciaBruta = pedidos.stream()
                .mapToDouble(p -> p.getDetalles() == null ? 0 : p.getDetalles().stream()
                        .mapToDouble(d -> d.getCantidad() * (d.getPlato().getPrecioVenta() - d.getPlato().getCostoProduccion()))
                        .sum())
                .sum();

        List<LiquidacionDeHaber> liquidaciones = liquidacionRepository.findByFestivalIdAndUnidadVentaId(festivalId, unidadVentaId);
        double totalSueldos = liquidaciones.stream().mapToDouble(LiquidacionDeHaber::getTotalACobrar).sum();

        double canon = unidadVentaService.calcularCanon(unidadVentaId);
        double rentabilidad = gananciaBruta - totalSueldos - canon;

        return new RentabilidadNetaDTO(festivalId, unidadVentaId, gananciaBruta, totalSueldos, canon, rentabilidad);
    }

    // PUNTO 7
    @Override
    public RentabilidadNetaDTO getRentabilidadNetaEntreFechas(Long festivalId, Long unidadVentaId, LocalDate desde, LocalDate hasta) {
        List<Pedido> pedidos = pedidoRepository.findByFestivalIdAndUnidadVentaId(festivalId, unidadVentaId)
                .stream()
                .filter(p -> p.getFechaPedido() != null
                        && !p.getFechaPedido().isBefore(desde)
                        && !p.getFechaPedido().isAfter(hasta))
                .toList();

        double gananciaBruta = pedidos.stream()
                .mapToDouble(p -> p.getDetalles() == null ? 0 : p.getDetalles().stream()
                        .mapToDouble(d -> d.getCantidad() * (d.getPlato().getPrecioVenta() - d.getPlato().getCostoProduccion()))
                        .sum())
                .sum();

        List<LiquidacionDeHaber> liquidaciones = liquidacionRepository.findByFestivalIdAndUnidadVentaId(festivalId, unidadVentaId);
        double totalSueldos = liquidaciones.stream().mapToDouble(LiquidacionDeHaber::getTotalACobrar).sum();
        double canon = unidadVentaService.calcularCanon(unidadVentaId);

        return new RentabilidadNetaDTO(festivalId, unidadVentaId, gananciaBruta, totalSueldos, canon, gananciaBruta - totalSueldos - canon);
    }
    // ================== PUNTO 8 - PLATO ESTRELLA (PlatoDTO) ==================
    @Override
    public PlatoDTO getPlatoEstrella(Long festivalId, Long unidadVentaId) {
        List<Pedido> pedidos = pedidoRepository.findByFestivalIdAndUnidadVentaId(festivalId, unidadVentaId);

        if (pedidos.isEmpty()) {
            throw new RuntimeException("No hay pedidos para esa unidad en ese festival");
        }

        Map<Long, Long> cantidadPorPlato = new HashMap<>();
        Map<Long, Plato> platoPorId = new HashMap<>();

        for (Pedido p : pedidos) {
            if (p.getDetalles() == null) continue;
            for (var d : p.getDetalles()) {
                Long id = d.getPlato().getId();
                platoPorId.put(id, d.getPlato());
                cantidadPorPlato.put(id, cantidadPorPlato.getOrDefault(id, 0L) + d.getCantidad());
            }
        }

        Long idGanador = cantidadPorPlato.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new RuntimeException("No hay platos vendidos"));

        Plato platoEstrella = platoPorId.get(idGanador);

        return PlatoDTO.builder()
                .id(platoEstrella.getId())
                .nombre(platoEstrella.getNombre())
                .precioVenta(platoEstrella.getPrecioVenta())
                .costoProduccion(platoEstrella.getCostoProduccion())
                .unidadVentaId(platoEstrella.getUnidadVenta() != null ? platoEstrella.getUnidadVenta().getId() : null)
                .nombreComercialUnidad(platoEstrella.getUnidadVenta() != null ? platoEstrella.getUnidadVenta().getNombreComercial() : null)
                .build();
    }

    @Override
    public List<ReporteVentaDTO> getRankingUnidadesPorRecaudacion(Long festivalId) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new RuntimeException("Festival no encontrado"));
        List<UnidadVenta> unidades = unidadVentaRepository.findByFestivalId(festivalId);

        return unidades.stream()
                .map(uv -> {
                    List<Pedido> pedidos = pedidoRepository.findByFestivalIdAndUnidadVentaId(festivalId, uv.getId());
                    double total = pedidos.stream()
                            .mapToDouble(p -> {
                                if (p.getDetalles() == null) return 0;
                                return p.getDetalles().stream()
                                        .mapToDouble(d -> d.getCantidad() * d.getPlato().getPrecioVenta())
                                        .sum();
                            })
                            .sum();
                    return new ReporteVentaDTO(
                            festivalId,
                            uv.getNombreComercial() + " - " + festival.getNombre(),
                            total,
                            pedidos.size()
                    );
                })
                .sorted((a, b) -> Double.compare(b.getRecaudacionTotal(), a.getRecaudacionTotal()))
                .toList();
    }

    @Override
    public List<ReporteMayoresCanonDTO> getUnidadesConMayorCanon(Long festivalId, int topX) {
        List<UnidadVenta> unidades = unidadVentaRepository.findByFestivalId(festivalId);
        return unidades.stream()
                .map(uv -> new ReporteMayoresCanonDTO(
                        uv.getId(),
                        uv.getNombreComercial(),
                        uv.getCodigoUnico(),
                        uv.getClass().getSimpleName(),
                        unidadVentaService.calcularCanon(uv.getId())
                ))
                .sorted((a,b) -> Double.compare(b.getCanon(), a.getCanon()))
                .limit(topX)
                .toList();
    }


}