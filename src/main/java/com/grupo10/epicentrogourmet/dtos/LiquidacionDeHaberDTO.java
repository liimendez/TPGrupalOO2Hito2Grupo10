package com.grupo10.epicentrogourmet.dtos;

import java.time.LocalDate;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LiquidacionDeHaberDTO {
    private Long id;
    private LocalDate fechaLiquidacion;
    private Long unidadVentaId;
    private String nombreUnidad;
    private Long personalId;
    private String nombrePersonal;
    private int diasDeTrabajo;
    private double totalACobrar;
}