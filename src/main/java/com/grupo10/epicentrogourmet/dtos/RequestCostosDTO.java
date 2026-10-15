package com.grupo10.epicentrogourmet.dtos;

import java.math.BigDecimal;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestCostosDTO {
    private BigDecimal costoSuperficie;
    private BigDecimal costoMontaje;
    private BigDecimal plusElectricidad;
    private BigDecimal sueldoBase;
    private BigDecimal plusCocinero;
    private BigDecimal plusAyudante;
    private BigDecimal plusLavaplatos;
}