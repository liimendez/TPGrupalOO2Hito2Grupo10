package com.grupo10.epicentrogourmet.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestDetallePedidoDTO {
    private int cantidad;
    private Long pedidoId;
    private Long platoId;
}