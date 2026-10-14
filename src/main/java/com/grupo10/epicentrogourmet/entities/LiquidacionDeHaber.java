package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class LiquidacionDeHaber {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaLiquidacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_venta_id", nullable = false)
    private UnidadVenta unidadVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id", nullable = false)
    private Personal personal;

    private int diasDeTrabajo;

    private double totalACobrar;
}
