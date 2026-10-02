package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;

@Entity
public class Plato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;
	private double precioVenta;
	private double costoProduccion;

	@ManyToOne
	private UnidadVenta unidadVenta; // el dueño de este plato

	public Plato() {
		// Constructor vacío requerido por Hibernate
	}

	public Plato(String nombre, double precioVenta, double costoProduccion) {
		this.nombre = nombre;
		this.precioVenta = precioVenta;
		this.costoProduccion = costoProduccion;
	}

	public double calcularGanancia() {
		return precioVenta - costoProduccion;
	}

	// getters y setters...
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public double getPrecioVenta() { return precioVenta; }
	public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
	public double getCostoProduccion() { return costoProduccion; }
	public void setCostoProduccion(double costoProduccion) { this.costoProduccion = costoProduccion; }
	public UnidadVenta getUnidadVenta() { return unidadVenta; }
	public void setUnidadVenta(UnidadVenta unidadVenta) { this.unidadVenta = unidadVenta; }

	@Override
	public String toString() {
		return String.format("%-30s | Venta: $%.2f | Costo: $%.2f",
				nombre, precioVenta, costoProduccion);
	}
}