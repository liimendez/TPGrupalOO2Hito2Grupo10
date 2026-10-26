package com.grupo10.epicentrogourmet.dtos;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestPedidoDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPedido;
    private Long unidadVentaId;

}