package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Cocinero extends Personal {

	private String especialidad;
	private double plusCategoria;

	public Cocinero() {
		super();
	}

	public Cocinero(String nombre, String apellido, String dni, LocalDate fechaDeNacimiento,
					LocalDate fechaDeIngreso, double sueldoBase, String especialidad, double plusCategoria) {
		super(nombre, apellido, dni, fechaDeNacimiento, fechaDeIngreso, sueldoBase);
		this.especialidad = especialidad;
		this.plusCategoria = plusCategoria;
	}

	//  metodo para calcular el plus de los cocineros...
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
			case "panaderia":
			case "postres":
			case "cocina fria":
				return getSueldoBase() * 0.10;
			case "fritura":
				return getSueldoBase() * 0.05;
			case "vegano":
				return getSueldoBase() * 0.18;
			default:
				return getSueldoBase() * 0.10;
		}
	}

	@Override
	public double calcularSueldo() {
		if (plusCategoria == 0) {
			plusCategoria = calcularPlusSegunEspecialidad();
		}
		// Fórmula del TP: Base + antigüedad ($5000 x año) + plus
		return getSueldoBase() + (getAntiguedad() * 5000) + plusCategoria;
	}

	public String getEspecialidad() { return especialidad; }
	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
		this.plusCategoria = calcularPlusSegunEspecialidad();
	}
	public double getPlusCategoria() { return plusCategoria; }
	public void setPlusCategoria(double plusCategoria) { this.plusCategoria = plusCategoria; }

	@Override
	public String toString() {
		return "cocinero: "+super.toString() + " [dni=" + getDni() + ", especialidad=" + especialidad + ", plusCategoria=" + plusCategoria + "] Sueldo Final: " + calcularSueldo();
	}
}