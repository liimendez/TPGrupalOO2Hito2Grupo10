package com.grupo10.epicentrogourmet.dtos;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestPersonalDTO {
    private String nombre;
    private String apellido;
    private String dni;
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaIngreso;

    private Long unidadAsignadaId;
    private Long credencialesId;

    private String tipoPersonal; // CAJERO / COCINERO

    private String turno; // MANIANA, NOCHE
    private String categoria; // cocinero, ayudante, lavaplatos
    private String especialidad;
}