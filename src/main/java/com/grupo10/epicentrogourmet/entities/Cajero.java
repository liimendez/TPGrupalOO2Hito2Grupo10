package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cajero extends Personal {

	@Enumerated(EnumType.STRING)
	private Turno turno;

	public enum Turno {
		MANIANA, TARDE, NOCHE
	}

	public Cajero(String nombre, String apellido, String dni, LocalDate fechaDeNacimiento,
				  LocalDate fechaDeIngreso, double sueldoBase, Turno turno, UnidadVenta unidadAsignada) {
		super(null, nombre, apellido, dni, fechaDeNacimiento, fechaDeIngreso, sueldoBase);
		this.turno = turno;
		setUnidadAsignada(unidadAsignada); // uso el setter del padre
	}

	@Override
	public double calcularSueldo() {
		return getSueldoBase() + (getAntiguedad() * 5000);
	}
}