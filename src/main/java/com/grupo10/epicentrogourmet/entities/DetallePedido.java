package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;

@Entity
public class DetallePedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Pedido pedido;

	@ManyToOne
	private Plato plato;

	private int cantidad;
	private double subtotal;

	public DetallePedido() {
		// Constructor vacio requerido por Hibernate
	}

	// ESTE es el único que vamos a usar en todo el proyecto
	public DetallePedido(Plato plato, int cantidad, Pedido pedido) {
		this.plato = plato;
		this.cantidad = cantidad;
		this.pedido = pedido;
		this.subtotal = calcularSubtotal();
	}

	public double calcularSubtotal() {
		if (plato == null) return 0;
		return plato.getPrecioVenta() * cantidad;
	}

	// Getters y Setters
	public Long getId() { return id; }
	public Pedido getPedido() { return pedido; }
	public void setPedido(Pedido pedido) { this.pedido = pedido; }
	public Plato getPlato() { return plato; }
	public void setPlato(Plato plato) { this.plato = plato; }
	public int getCantidad() { return cantidad; }
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
		this.subtotal = calcularSubtotal();
	}
	public double getSubtotal() { return subtotal; }
}