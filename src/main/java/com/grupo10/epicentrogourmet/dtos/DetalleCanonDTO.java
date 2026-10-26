package com.grupo10.epicentrogourmet.dtos;

import java.math.BigDecimal;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleCanonDTO {
    private Long unidadId;
    private String nombreComercial;
    private Double superficieM2;
    private Long diasFestival;
    private BigDecimal costoSuperficieTotal; // superficie * 500
    private BigDecimal costoMontaje;         // 10
    private BigDecimal costoElectricidad;   // 2000 * dias
    private BigDecimal totalCanon;
}