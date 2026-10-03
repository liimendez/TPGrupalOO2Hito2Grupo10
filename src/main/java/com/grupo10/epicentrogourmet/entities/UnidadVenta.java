package com.grupo10.epicentrogourmet.entities;

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
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombreComercial;
	private double superficieM2;

	@Column(unique = true, length = 10)
	private String codigoUnico;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "festival_id")
	private Festival festival;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsable_id")
	private Personal responsable;


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

	public UnidadVenta(String nombreComercial, double superficieM2, String codigoUnico, Festival festival, Personal responsable) {
		this.nombreComercial = nombreComercial;
		this.superficieM2 = superficieM2;
		setCodigoUnico(codigoUnico);
		this.festival = festival;
		this.responsable = responsable;
	}

	public static boolean validarCodigo(String codigo) {
		return codigo != null && codigo.matches("^[a-zA-Z0-9]{10}$");
	}

	public void setCodigoUnico(String codigoUnico) {
		if (!validarCodigo(codigoUnico)) {
			throw new IllegalArgumentException("El codigo unico debe tener exactamente 10 caracteres alfanumericos");
		}
		this.codigoUnico = codigoUnico;
	}


	public void asignarStaff(Personal empleado) {
		staff.add(empleado);
		empleado.setUnidadAsignada(this);
	}

	public void agregarPlato(Plato plato) {
		platosOfrecidos.add(plato);
		plato.setUnidadVenta(this);
	}
}