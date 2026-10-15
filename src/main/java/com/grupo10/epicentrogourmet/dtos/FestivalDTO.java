package com.grupo10.epicentrogourmet.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FestivalDTO {
    private Long id;
    private String nombre;
    private String temporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long costosId;
    private int cantidadUnidadesVenta;
    private LocalDateTime createdAt;
}