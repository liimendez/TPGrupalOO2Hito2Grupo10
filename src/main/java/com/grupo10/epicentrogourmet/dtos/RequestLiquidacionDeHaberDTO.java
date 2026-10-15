package com.grupo10.epicentrogourmet.dtos;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestLiquidacionDeHaberDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaLiquidacion;
    private Long unidadVentaId;
    private Long personalId;
    private int diasDeTrabajo;
}