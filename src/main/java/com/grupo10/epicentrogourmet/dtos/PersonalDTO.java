package com.grupo10.epicentrogourmet.dtos;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.*;
import com.grupo10.epicentrogourmet.entities.PersonalType;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PersonalDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaIngreso;
    private String email; // para crear el Usuario
    private boolean activo;


    private String tipoPersonal; // "CAJERO", "COCINERO", "LAVAPLATOS", "AYUDANTE"


    private String turno; // si es cajero
    private String categoria; // si es cocinero
    private String especialidad; // si es cocinero
    private Long unidadAsignadaId;
    private String nombreUnidadAsignada;
}