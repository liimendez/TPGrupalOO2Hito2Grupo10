package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate fechaTransaccion;

	@ManyToOne
	private UnidadVenta unidadVenta;

	@ManyToOne
	private Cajero cajero; // sera el encargado de recaudar el dinero

	@ManyToOne
	private Festival festival; // lo necesitás para el reporte del Hito 2

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<DetallePedido> detalles = new HashSet<>();

	public Pedido() {}

	public Pedido(LocalDate fechaTransaccion, UnidadVenta unidadVenta, Cajero cajero) {
		this.fechaTransaccion = fechaTransaccion;
		this.unidadVenta = unidadVenta;
		this.cajero = cajero;
	}

	public void agregarDetalle(Plato plato, int cantidad) {
		if (plato == null || cantidad <= 0) return;
		for (DetallePedido d : this.detalles) {
			if (d.getPlato() != null && d.getPlato().getId() != null && d.getPlato().getId().equals(plato.getId())) {
				d.setCantidad(d.getCantidad() + cantidad);
				return;
			}
		}
		DetallePedido nuevo = new DetallePedido(plato, cantidad, this);
		this.detalles.add(nuevo);
	}

	// getters/setters
	public Cajero getCajero() { return cajero; }
	public void setCajero(Cajero cajero) { this.cajero = cajero; }
}