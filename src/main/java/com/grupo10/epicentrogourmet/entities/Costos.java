package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Costos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concepto; // Ej: Alquiler carpa, Luz, Insumos
    private double monto;
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_venta_id")
    private UnidadVenta unidadVenta;

    public Costos(String concepto, double monto, LocalDate fecha, Festival festival, UnidadVenta unidadVenta) {
        this.concepto = concepto;
        this.monto = monto;
        this.fecha = fecha;
        this.festival = festival;
        this.unidadVenta = unidadVenta;
    }

    public Costos(String concepto, double monto) {
        this.concepto = concepto;
        this.monto = monto;
        this.fecha = LocalDate.now();
    }
}
