package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombreComercial;
	private double superficieM2;
	@Column(unique = true)
	private String codigoUnico;

	@ManyToOne
	private Festival festival;

	@ManyToOne
	private Personal responsable;

	protected UnidadVenta() {}

	public UnidadVenta(String nombreComercial, double superficieM2, String codigoUnico, Festival festival, Personal responsable) {
		this.nombreComercial = nombreComercial;
		this.superficieM2 = superficieM2;
		this.codigoUnico = codigoUnico;
		this.festival = festival;
		this.responsable = responsable;
	}

	public Long getId() { return id; }
	public String getNombreComercial() { return nombreComercial; }
}