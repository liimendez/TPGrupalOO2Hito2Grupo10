package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate fechaPedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unidad_venta_id", nullable = false)
	private UnidadVenta unidadVenta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "metodo_pago_id", nullable = true)
	private MetodoPago metodoPago;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("id ASC")
	private Set<DetallePedido> detalles = new HashSet<>();

	/*
	@Transient
	public double getTotal() {
		return detalles.stream()
				.mapToDouble(d -> d.getSubtotal())
				.sum();
	}
	*/

	@Transient
	public double getTotal() {
		double subtotal = detalles.stream()
				.mapToDouble(d -> d.getSubtotal())
				.sum();

		if (metodoPago != null && metodoPago.getDescuentoPorcentaje() > 0) {
			double descuento = subtotal * (metodoPago.getDescuentoPorcentaje() / 100.0);
			return subtotal - descuento;
		}

		return subtotal;
	}

	@Transient
	public double getCostoTotal() {
		return detalles.stream()
				.mapToDouble(d -> d.getCantidad() * d.getPlato().getCostoProduccion())
				.sum();
	}
}