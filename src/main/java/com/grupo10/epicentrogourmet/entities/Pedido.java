package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate fechaTransaccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unidad_venta_id")
	private UnidadVenta unidadVenta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cajero_id")
	private Cajero cajero;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<DetallePedido> detalles = new HashSet<>();

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public Pedido(LocalDate fechaTransaccion, UnidadVenta unidadVenta, Cajero cajero) {
		this.fechaTransaccion = fechaTransaccion;
		this.unidadVenta = unidadVenta;
		this.cajero = cajero;
	}


	public void agregarDetalle(Plato plato, int cantidad) {
		if (plato == null || cantidad <= 0) return;
		for (DetallePedido d : this.detalles) {
			if (d.getPlato() != null && plato.getId() != null && d.getPlato().getId().equals(plato.getId())) {
				d.setCantidad(d.getCantidad() + cantidad);
				return;
			}
		}
		DetallePedido nuevo = new DetallePedido(plato, cantidad, this);
		this.detalles.add(nuevo);
	}

	public double calcularTotal() {
		return detalles.stream()
				.mapToDouble(d -> d.getPlato().getPrecioVenta() * d.getCantidad())
				.sum();
	}

	@Transient
	public Festival getFestival() {
		return unidadVenta != null ? unidadVenta.getFestival() : null;
	}
}