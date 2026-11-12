package com.grupo10.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.grupo10.epicentrogourmet.dtos.*;
import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.repositories.*;
import com.grupo10.epicentrogourmet.services.ICostosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo10.epicentrogourmet.services.IFestivalService;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;

@Service("festivalService")
public class FestivalService implements IFestivalService {

    @Autowired private IFestivalRepository festivalRepository;
    @Autowired private IPedidoRepository pedidoRepository;
    @Autowired private ILiquidacionDeHaberRepository liquidacionRepository;
    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @Autowired private IUnidadVentaService unidadVentaService;
    @Autowired private ICostosService costosService;
    @Autowired private IPersonalRepository personalRepository;

    @Override public List<Festival> getAll() { return festivalRepository.findAll(); }
    @Override public Optional<Festival> findById(Long id) { return festivalRepository.findById(id); }
    @Override public Festival insertOrUpdate(Festival festival) { return festivalRepository.save(festival); }
    @Override
    @Transactional
    public boolean remove(Long id) {
        if (!festivalRepository.existsById(id)) return false;
        Festival festival = festivalRepository.findById(id).orElseThrow();
        List<UnidadVenta> unidades = unidadVentaRepository.findByFestivalId(id);
        for (UnidadVenta uv : unidades) {
            if (uv.getFestivales() != null) {
                uv.getFestivales().removeIf(f -> f.getId().equals(id));
                unidadVentaRepository.save(uv);
            }
        }
        festivalRepository.deleteById(id);
        return true;
    }

    // PUNTO 5 - Recaudación Total - FIX: usa subtotal congelado
    @Override
    public ReporteVentaDTO getRecaudacionTotal(Long festivalId) {
        Festival festival = festivalRepository.findById(festivalId).orElseThrow(() -> new RuntimeException("Festival no encontrado"));
        List<Pedido> pedidos = pedidoRepository.findByFestivalId(festivalId);
        double total = pedidos.stream()
                .mapToDouble(p -> p.getDetalles() == null ? 0 : p.getDetalles().stream()
                        .mapToDouble(d -> d.getSubtotal()).sum())
                .sum();
        return new ReporteVentaDTO(festival.getId(), festival.getNombre(), total, pedidos.size());
    }

    // PUNTO 6 - Cálculo de Rentabilidad Neta - FIX: usa subtotal congelado
    @Override
    public RentabilidadNetaDTO getRentabilidadNeta(Long festivalId, Long unidadVentaId) {
        Festival festival = festivalRepository.findById(festivalId).orElseThrow(() -> new RuntimeException("No se encontro el Festival"));
        UnidadVenta unidad = unidadVentaRepository.findById(unidadVentaId).orElseThrow(() -> new RuntimeException("No se encontro la UnidadVenta"));
        List<Pedido> pedidos = pedidoRepository.findPedidosParaRentabilidad(unidad, festival.getFechaInicio(), festival.getFechaFin());
        double gananciaBruta = pedidos.stream()
                .mapToDouble(p -> p.getDetalles() == null ? 0 : p.getDetalles().stream()
                        .mapToDouble(d -> d.getSubtotal() - (d.getCantidad() * d.getPlato().getCostoProduccion()))
                        .sum())
                .sum();
        List<LiquidacionDeHaber> liquidaciones = liquidacionRepository.findLiquidacionesParaRentabilidad(unidad, festival.getFechaInicio(), festival.getFechaFin());
        double totalSueldos = liquidaciones.stream()
                .mapToDouble(LiquidacionDeHaber::getTotalACobrar)
                .sum();
        double canon = costosService.calcularCanon(unidadVentaId).getTotalCanon().doubleValue();
        double rentabilidadNeta = gananciaBruta - totalSueldos - canon;
        return new RentabilidadNetaDTO(festivalId, unidadVentaId, gananciaBruta, totalSueldos, canon, rentabilidadNeta);
    }

    // PUNTO 7 - Cálculo de Rentabilidad Neta entre dos fechas - FIX: usa subtotal congelado
    @Override
    public RentabilidadNetaDTO getRentabilidadNetaEntreFechas(Long festivalId, Long unidadVentaId, LocalDate desde, LocalDate hasta) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new RuntimeException("No se encontro el Festival"));
        if (desde.isBefore(festival.getFechaInicio()) || hasta.isAfter(festival.getFechaFin()) || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El rango de fechas debe estar indicado entre el Inicio y el Fin del Festival.");
        }
        UnidadVenta unidad = unidadVentaRepository.findById(unidadVentaId)
                .orElseThrow(() -> new RuntimeException("No se encontro la UnidadVenta"));
        List<Pedido> pedidos = pedidoRepository.findPedidosParaRentabilidad(unidad, desde, hasta);
        double gananciaBruta = pedidos.stream()
                .mapToDouble(p -> p.getDetalles() == null ? 0 : p.getDetalles().stream()
                        .mapToDouble(d -> d.getSubtotal() - (d.getCantidad() * d.getPlato().getCostoProduccion()))
                        .sum())
                .sum();
        List<LiquidacionDeHaber> liquidaciones = liquidacionRepository.findLiquidacionesParaRentabilidad(unidad, desde, hasta);
        double totalSueldos = liquidaciones.stream()
                .mapToDouble(LiquidacionDeHaber::getTotalACobrar)
                .sum();
        double canon = costosService.calcularCanon(unidadVentaId).getTotalCanon().doubleValue();
        double rentabilidadNeta = gananciaBruta - totalSueldos - canon;
        return new RentabilidadNetaDTO(festivalId, unidadVentaId, gananciaBruta, totalSueldos, canon, rentabilidadNeta);
    }

    // PUNTO 8 - Plato Estrella
    @Override
    public PlatoDTO getPlatoEstrella(Long festivalId, Long unidadVentaId) {
        List<Pedido> pedidos = pedidoRepository.findPedidosPlatoEstrellaPorFestival(festivalId, unidadVentaId);
        if (pedidos.isEmpty()) throw new RuntimeException("No hay pedidos para esa unidad en ese festival");
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
        Long idGanador = cantidadPorPlato.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElseThrow(() -> new RuntimeException("No hay platos vendidos"));
        Plato platoEstrella = platoPorId.get(idGanador);
        return PlatoDTO.builder().id(platoEstrella.getId()).nombre(platoEstrella.getNombre()).precioVenta(platoEstrella.getPrecioVenta()).costoProduccion(platoEstrella.getCostoProduccion()).unidadVentaId(platoEstrella.getUnidadVenta() != null ? platoEstrella.getUnidadVenta().getId() : null).nombreComercialUnidad(platoEstrella.getUnidadVenta() != null ? platoEstrella.getUnidadVenta().getNombreComercial() : null).cantidadVendida(cantidadPorPlato.get(idGanador)).build();
    }

    // PUNTO 3 - Ranking por Recaudación (ADMIN) - FIX: usa subtotal congelado
    @Override
    public List<ReporteVentaDTO> getRankingUnidadesPorRecaudacion(Long festivalId) {
        Festival festival = festivalRepository.findById(festivalId).orElseThrow(() -> new RuntimeException("Festival no encontrado"));
        List<UnidadVenta> unidades = unidadVentaRepository.findByFestivalId(festivalId);
        return unidades.stream()
                .map(uv -> {
                    List<Pedido> pedidos = pedidoRepository.findByFestivalIdAndUnidadVentaId(festivalId, uv.getId());
                    double total = pedidos.stream().mapToDouble(p -> p.getDetalles() == null ? 0 : p.getDetalles().stream().mapToDouble(d -> d.getSubtotal()).sum()).sum();
                    return new ReporteVentaDTO(festivalId, uv.getNombreComercial() + " - " + festival.getNombre(), total, pedidos.size());
                })
                .sorted((a, b) -> Double.compare(b.getRecaudacionTotal(), a.getRecaudacionTotal()))
                .toList();
    }

    // PUNTO 4 - Mayor Canon (ADMIN)
    @Override
    public List<ReporteMayoresCanonDTO> getUnidadesConMayorCanon(Long festivalId, int topX) {
        List<UnidadVenta> unidades = unidadVentaRepository.findByFestivalId(festivalId);
        return unidades.stream()
                .map(uv -> new ReporteMayoresCanonDTO(uv.getId(), uv.getNombreComercial(), uv.getCodigoUnico(), uv.getClass().getSimpleName(), unidadVentaService.calcularCanon(uv.getId())))
                .sorted((a,b) -> Double.compare(b.getCanon(), a.getCanon()))
                .limit(topX)
                .toList();
    }

    @Override
    public Optional<Festival> findByIdWithUnidades(Long id) {
        return festivalRepository.findByIdWithUnidades(id);
    }

    // AGREGAR UNIDAD CON VALIDACIONES DE DUPLICADO Y SOLAPAMIENTO
    @Override
    @Transactional
    public void agregarUnidadAFestival(Long festivalId, Long unidadVentaId) {
        Festival festival = festivalRepository.findById(festivalId).orElseThrow(() -> new RuntimeException("Festival no encontrado"));
        UnidadVenta unidad = unidadVentaRepository.findById(unidadVentaId).orElseThrow(() -> new RuntimeException("Unidad no encontrada"));
        if (unidad.getFestivales().stream().anyMatch(f -> f.getId().equals(festivalId))) {
            throw new IllegalArgumentException("DUPLICADO: La unidad '" + unidad.getNombreComercial() + "' ya está registrada en este festival");
        }
        for (Festival fExistente : unidad.getFestivales()) {
            if (fechasSeSolapan(fExistente.getFechaInicio(), fExistente.getFechaFin(), festival.getFechaInicio(), festival.getFechaFin())) {
                throw new IllegalArgumentException("SOLAPAMIENTO: La unidad '" + unidad.getNombreComercial() + "' ya está en el festival '" + fExistente.getNombre() + "' (" + fExistente.getFechaInicio() + " al " + fExistente.getFechaFin() + ") que se solapa");
            }
        }
        unidad.getFestivales().add(festival);
        unidadVentaRepository.save(unidad);
    }

    private boolean fechasSeSolapan(LocalDate inicio1, LocalDate fin1, LocalDate inicio2, LocalDate fin2) {
        if (inicio1 == null || fin1 == null || inicio2 == null || fin2 == null) return false;
        return !fin1.isBefore(inicio2) && !fin2.isBefore(inicio1);
    }

    @Override
    public Map<String, Object> getEstadoActualEmpleado(String correo) {
        UnidadVenta unidad = unidadVentaRepository.findUnidadConFestivalesByEmpleadoCorreo(correo)
                .orElse(null);

        Festival festivalActivo = null;
        LocalDate hoy = LocalDate.now();

        if (unidad != null) {
            for (Festival f : unidad.getFestivales()) {
                if (!hoy.isBefore(f.getFechaInicio()) && !hoy.isAfter(f.getFechaFin())) {
                    festivalActivo = f;
                    break;
                }
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("unidad", unidad);
        resultado.put("festival", festivalActivo);
        return resultado;
    }

    public CierreCajaDTO getCierreCajaDiario(String correoCajero) {
        Personal personal = personalRepository.findByCredencialesCorreo(correoCajero)
                .orElseThrow(() -> new RuntimeException("No se encontro al Cajero"));

        UnidadVenta unidad = personal.getUnidadAsignada();
        if (unidad == null) {
            throw new RuntimeException("El cajero no tiene una unidad de venta asignada");
        }

        LocalDate hoy = LocalDate.now();

        List<Pedido> pedidosHoy = pedidoRepository.findAll().stream()
                .filter(p -> p.getUnidadVenta() != null && p.getUnidadVenta().getId().equals(unidad.getId()))
                .filter(p -> p.getFechaPedido() != null && p.getFechaPedido().isEqual(hoy))
                .toList();

        double totalRecaudado = pedidosHoy.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();

        return CierreCajaDTO.builder()
                .fecha(hoy)
                .nombreUnidad(unidad.getNombreComercial())
                .nombreCajero(personal.getNombre() + " " + personal.getApellido())
                .totalRecaudado(totalRecaudado)
                .cantidadPedidos(pedidosHoy.size())
                .build();
    }
}