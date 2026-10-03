package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class FoodTruck extends UnidadVenta {

	private String patente;
	private boolean requiereConexionElectrica;

	public FoodTruck(String nombreComercial, double superficieM2, String codigoUnico,
					 Festival festival, Personal responsable, String patente,
					 boolean requiereConexionElectrica) {
		super(nombreComercial, superficieM2, codigoUnico, festival, responsable);
		this.patente = patente;
		this.requiereConexionElectrica = requiereConexionElectrica;
	}




}