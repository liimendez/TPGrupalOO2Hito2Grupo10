package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;

@Entity
public class FoodTruck extends UnidadVenta {

	private String patente;
	private boolean requiereConexionElectrica;

	protected FoodTruck() {
		super();
	}

	public FoodTruck(String nombreComercial, double superficieM2, String codigoUnico, Festival festival,
					 Personal responsable, String patente, boolean requiereConexionElectrica) {
		super(nombreComercial, superficieM2, codigoUnico, festival, responsable);
		this.patente = patente;
		this.requiereConexionElectrica = requiereConexionElectrica;
	}

	public String getPatente() { return patente; }
	public void setPatente(String patente) { this.patente = patente; }
	public boolean isRequiereConexionElectrica() { return requiereConexionElectrica; }
	public void setRequiereConexionElectrica(boolean requiereConexionElectrica) { this.requiereConexionElectrica = requiereConexionElectrica; }
}