package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Cajero extends Personal {

	@Enumerated(EnumType.STRING)
	private Turno turno;

	public enum Turno {
		MANIANA, NOCHE
	}

	public Cajero() {
		super();
	}

	public Cajero(String nombre, String apellido, String dni, LocalDate fechaDeNacimiento,
				  LocalDate fechaDeIngreso, double sueldoBase, Turno turno, UnidadVenta unidadAsignada) {
		super(nombre, apellido, dni, fechaDeNacimiento, fechaDeIngreso, sueldoBase);
		this.turno = turno;
		this.unidadAsignada = unidadAsignada;
	}

	public Turno getTurno() {
		return turno;
	}

	public void setTurno(Turno turno) {
		this.turno = turno;
	}

	@Override
	public double calcularSueldo() {
		// TP: Sueldo Base: $100.000 + 1 año antigüedad $5000
		return getSueldoBase() + (getAntiguedad() * 5000);
	}

	@Override
	public String toString() {
		return "cajero : "+super.toString()+
				", turno=" + turno +
				", unidad=" + (unidadAsignada != null ? unidadAsignada.getId() : "SIN UNIDAD") + "]";
	}
}