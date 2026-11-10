package com.grupo10.epicentrogourmet.entities;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Costos {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal costoSuperficie; // areaSup * $500
    private BigDecimal costoMontaje; // $10
    private BigDecimal plusElectricidad; // $2000
    private BigDecimal sueldoBase; // 100.000

    private BigDecimal valorAntiguedadPorAnio; // $5000 por año
    private Integer diasPorTramo; // 15 días = 1 tramo
    private BigDecimal factorFestival; // 0.5 = 50%



}