package com.grupo10.epicentrogourmet.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetallePedidoDTO {
    private Long id;
    private int cantidad;
    private Long pedidoId;
    private Long platoId;
    private String nombrePlato;
    private double subtotal;
}