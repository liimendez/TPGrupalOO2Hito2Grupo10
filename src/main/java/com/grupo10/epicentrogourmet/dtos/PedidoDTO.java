package com.grupo10.epicentrogourmet.dtos;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoDTO {
    private Long id;
    private LocalDate fechaPedido;
    private Long unidadVentaId;
    private String nombreUnidad;
    private int cantidadDetalles;
    private double total;
}