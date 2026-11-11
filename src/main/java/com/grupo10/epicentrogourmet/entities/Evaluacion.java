package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "evaluacion")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "personal_id", nullable = false)
    private Personal evaluado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    @Column(nullable = false)
    private LocalDate fechaEvaluacion;

    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer puntualidad;

    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer atencionCliente;

    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer higienePresentacion;

    @Column(nullable = false)
    private double promedio;

    @Column(length = 500)
    private String observacion;
}