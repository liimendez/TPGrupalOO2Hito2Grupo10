
package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Festival {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String temporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL)
    private Set<UnidadVenta> unidadesVenta = new HashSet<>();

    @OneToMany(mappedBy = "festival")
    private Set<Pedido> pedidos = new HashSet<>();

    public Festival() {}

    // getters/setters
    public Set<UnidadVenta> getUnidadesVenta() { return unidadesVenta; }
    public void setUnidadesVenta(Set<UnidadVenta> unidadesVenta) { this.unidadesVenta = unidadesVenta; }

    public void setFechaFin(LocalDate fechaFin) {
        if (fechaFin != null && fechaInicio != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha fin no puede ser antes del inicio");
        }
        this.fechaFin = fechaFin;
    }
}