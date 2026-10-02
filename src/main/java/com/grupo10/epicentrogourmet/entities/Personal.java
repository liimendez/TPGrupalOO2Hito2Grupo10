package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Personal {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private String apellido;
	private String dni;
	private LocalDate fechaDeNacimiento;
	private LocalDate fechaDeIngreso;
	protected double sueldoBase; // protected para que lo vean los hijos

	@ManyToOne
	protected UnidadVenta unidadAsignada;

	protected Personal() {}

	public Personal(String nombre, String apellido, String dni, LocalDate fechaDeNacimiento, LocalDate fechaDeIngreso, double sueldoBase) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
		this.fechaDeNacimiento = fechaDeNacimiento;
		this.fechaDeIngreso = fechaDeIngreso;
		this.sueldoBase = sueldoBase;
	}

	public int getAntiguedad() {
		if (fechaDeIngreso == null) return 0;
		return Period.between(fechaDeIngreso, LocalDate.now()).getYears();
	}

	public abstract double calcularSueldo();

	// GETTERS QUE TE FALTAN
	public Long getId() { return id; }
	public String getDni() { return dni; }
	public double getSueldoBase() { return sueldoBase; }
	public UnidadVenta getUnidadAsignada() { return unidadAsignada; }
	public void setUnidadAsignada(UnidadVenta u) { this.unidadAsignada = u; }
}