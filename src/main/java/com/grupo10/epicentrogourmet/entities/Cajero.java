package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cajero extends Personal {

	@Enumerated(EnumType.STRING)
	private Turno turno;

	public enum Turno {
		MANIANA, NOCHE
	}


}