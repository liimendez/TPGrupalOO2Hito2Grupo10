package com.grupo10.epicentrogourmet.dtos;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestFestivalDTO {
    private String nombre;
    private String temporada;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    private Long costosId; // opcional

    private String imagenUrl; // NUEVO para uv y otros
}