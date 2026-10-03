package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Personal {

	private String especialidad;
	private double plusCategoria;

	public Cocinero(String nombre, String apellido, String dni, LocalDate fechaDeNacimiento,
					LocalDate fechaDeIngreso, double sueldoBase, String especialidad,
					UnidadVenta unidadAsignada) {
		super(null, nombre, apellido, dni, fechaDeNacimiento, fechaDeIngreso, sueldoBase);
		this.especialidad = especialidad;
		this.plusCategoria = calcularPlusSegunEspecialidad();
		setUnidadAsignada(unidadAsignada);
	}

	private double calcularPlusSegunEspecialidad() {
		if (especialidad == null) return 0;
		switch (especialidad.toLowerCase().trim()) {
			case "parrilla":
			case "parrillero":
				return getSueldoBase() * 0.20;
			case "sushi":
			case "wok":
				return getSueldoBase() * 0.25;
			case "pizzas":
			case "pastas":
				return getSueldoBase() * 0.15;
			case "vegano":
				return getSueldoBase() * 0.18;
			default:
				return getSueldoBase() * 0.10;
		}
	}

	@Override
	public double calcularSueldo() {
		// Si cambió la especialidad con el setter, recalcula
		if (plusCategoria == 0 && especialidad != null) {
			plusCategoria = calcularPlusSegunEspecialidad();
		}
		return getSueldoBase() + (getAntiguedad() * 5000) + plusCategoria;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
		this.plusCategoria = calcularPlusSegunEspecialidad();
	}
}