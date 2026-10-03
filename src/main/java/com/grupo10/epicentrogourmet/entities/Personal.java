package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED) // Igual que el de Oscar - https://www.baeldung.com/hibernate-inheritance
public abstract class Personal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;
	private String apellido;

	@Column(unique = true)
	private String dni;

	private LocalDate fechaDeNacimiento;
	private LocalDate fechaDeIngreso;

	@Column(nullable = false)
	private double sueldoBase;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unidad_asignada_id")
	private UnidadVenta unidadAsignada;

	// LO MISMO QUE EL DE OSCAR
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;


	public Personal(Long id, String nombre, String apellido, String dni, LocalDate fechaDeNacimiento, LocalDate fechaDeIngreso, double sueldoBase) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
		this.fechaDeNacimiento = fechaDeNacimiento;
		this.fechaDeIngreso = fechaDeIngreso;
		this.sueldoBase = sueldoBase;
	}

	public Personal(String nombre, String apellido, String dni) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
	}

	public int getAntiguedad() {
		if (fechaDeIngreso == null) return 0;
		return Period.between(fechaDeIngreso, LocalDate.now()).getYears();
	}

	public abstract double calcularSueldo();
}