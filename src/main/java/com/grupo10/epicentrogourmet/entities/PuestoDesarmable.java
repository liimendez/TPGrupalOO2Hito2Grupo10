package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class PuestoDesarmable extends UnidadVenta {

	private int cantidadCarpas;
	private int tiempoMontajeMin;

	public PuestoDesarmable(String nombreComercial, double superficieM2, String codigoUnico,
							Festival festival, Personal responsable, int cantidadCarpas,
							int tiempoMontajeMin) {
		super(nombreComercial, superficieM2, codigoUnico, festival, responsable);
		this.cantidadCarpas = cantidadCarpas;
		this.tiempoMontajeMin = tiempoMontajeMin;
	}
}