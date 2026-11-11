package com.grupo10.epicentrogourmet.services.implementation;

import com.grupo10.epicentrogourmet.dto.VentaDiariaDTO;
import com.grupo10.epicentrogourmet.entities.Festival;
import com.grupo10.epicentrogourmet.entities.Pedido;
import com.grupo10.epicentrogourmet.repositories.IFestivalRepository;
import com.grupo10.epicentrogourmet.repositories.IPedidoRepository;
import com.grupo10.epicentrogourmet.services.IHistorialVentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class HistorialVentasService implements IHistorialVentasService {

    @Autowired
    private IPedidoRepository pedidoRepository;

    @Autowired
    private IFestivalRepository festivalRepository;

    @Override
    public List<VentaDiariaDTO> obtenerHistorial(Long festivalId, Long unidadVentaId, LocalDate desde, LocalDate hasta) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));

        List<Pedido> pedidos = pedidoRepository.findByFestivalIdConDetalles(festivalId);

        Map<LocalDate, List<Pedido>> porFecha = pedidos.stream()
                .filter(p -> p.getFechaPedido() != null)
                .filter(p -> p.getUnidadVenta() != null && unidadVentaId.equals(p.getUnidadVenta().getId()))
                // Solo pedidos dentro de las fechas del festival
                .filter(p -> festival.getFechaInicio() == null || !p.getFechaPedido().isBefore(festival.getFechaInicio()))
                .filter(p -> festival.getFechaFin() == null || !p.getFechaPedido().isAfter(festival.getFechaFin()))

                .filter(p -> desde == null || !p.getFechaPedido().isBefore(desde))
                .filter(p -> hasta == null || !p.getFechaPedido().isAfter(hasta))
                .collect(Collectors.groupingBy(Pedido::getFechaPedido, TreeMap::new, Collectors.toList()));

        return porFecha.entrySet().stream()
                .map(e -> new VentaDiariaDTO(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue().stream().mapToDouble(Pedido::getTotal).sum()
                ))
                .toList();
    }
}