package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class DetallePedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int cantidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plato_id")
	private Plato plato;


	public DetallePedido(Plato plato, int cantidad, Pedido pedido) {
		this.plato = plato;
		this.cantidad = cantidad;
		this.pedido = pedido;
	}

	public double calcularSubtotal() {
		return plato != null ? plato.getPrecioVenta() * cantidad : 0;
	}

	@Transient
	public double getSubtotal() {
		return calcularSubtotal();
	}
}