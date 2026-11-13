package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre comercial es obligatorio")
	@Column(nullable = false)
	private String nombreComercial;

	@Positive(message = "La superficie debe ser mayor a 0")
	@Column(nullable = false)
	private double superficieM2;

	@Column(unique = true, length = 10)
	@Size(max = 10, message = "El código único no puede superar los 10 caracteres")
	private String codigoUnico;

	@ManyToMany
	@JoinTable(
			name = "festival_unidad_venta",
			joinColumns = @JoinColumn(name = "unidad_venta_id"),
			inverseJoinColumns = @JoinColumn(name = "festival_id")
	)
	@OrderBy("nombre ASC")
	private Set<Festival> festivales = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsable_id")
	private Usuario responsable;

	@OneToMany(mappedBy = "unidadAsignada", fetch = FetchType.LAZY)
	@OrderBy("id ASC")
	private Set<Personal> staff = new HashSet<>();

	@OneToMany(mappedBy = "unidadVenta")
	@OrderBy("id ASC")
	private Set<Plato> platosOfrecidos;

	@OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
	@OrderBy("id ASC")
	private Set<Pedido> pedidos = new HashSet<>();

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}