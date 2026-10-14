package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombreComercial;
	private double superficieM2;

	@Column(unique = true, length = 10)
	private String codigoUnico;

	@ManyToMany
	@JoinTable(
			name = "festival_unidad_venta",
			joinColumns = @JoinColumn(name = "unidad_venta_id"),
			inverseJoinColumns = @JoinColumn(name = "festival_id")
	)
	private Set<Festival> festivales = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsable_id")
	private Usuario responsable;

	@OneToMany(mappedBy = "unidadAsignada", fetch = FetchType.LAZY)
	private Set<Personal> staff = new HashSet<>();

	@OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
	private Set<Plato> platosOfrecidos = new HashSet<>();

	@OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
	private Set<Pedido> pedidos = new HashSet<>();

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}