package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "categorias_cocina")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoriaCocina {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private BigDecimal plusFijo;

}