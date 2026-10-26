package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;

@Entity @Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter @NoArgsConstructor
@Table(name = "personal")
@SQLDelete(sql = "UPDATE personal SET activo = false WHERE id = ?")

public abstract class Personal {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre, apellido, dni;
	private LocalDate fechaNacimiento, fechaIngreso;
	private boolean activo = true;

	@OneToOne @JoinColumn(name = "usuario_id", unique = true)
	private Usuario credenciales;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "unidad_asignada_id", nullable = false)
	private UnidadVenta unidadAsignada;
}