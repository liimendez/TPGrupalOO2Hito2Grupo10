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

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<DetallePedido> detalles = new HashSet<>();
}