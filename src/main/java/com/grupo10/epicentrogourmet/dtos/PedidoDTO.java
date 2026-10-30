package com.grupo10.epicentrogourmet.dtos;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoDTO {
    private Long id;
    private LocalDate fechaPedido;
    private Long unidadVentaId;
    private String nombreUnidad;
    private String codigoUnico;
    private String nombreFestival;
    private int cantidadDetalles;
    private double total;
    private double costoTotal;
    private List<DetallePedidoDTO> detalles;
}