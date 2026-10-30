package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "cocinero")
public class Cocinero extends Personal {

	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private CategoriaCocina categoriaCocina;
}