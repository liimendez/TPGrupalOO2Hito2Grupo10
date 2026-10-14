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
}