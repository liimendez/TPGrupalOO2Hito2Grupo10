package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Plato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;
	private double precioVenta;
	private double costoProduccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unidad_venta_id")
	private UnidadVenta unidadVenta;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public Plato(String nombre, double precioVenta, double costoProduccion) {
		this.nombre = nombre;
		this.precioVenta = precioVenta;
		this.costoProduccion = costoProduccion;
	}

	public double calcularGanancia() {
		return precioVenta - costoProduccion;
	}

	@Override
	public String toString() {
		return String.format("%-30s | Venta: $%.2f | Costo: $%.2f",
				nombre, precioVenta, costoProduccion);
	}
}