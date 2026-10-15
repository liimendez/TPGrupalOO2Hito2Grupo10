package com.grupo10.epicentrogourmet.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioDTO {
    private Long id;
    private String correo;
    private Long rolId;
    private String nombreRol;
    private boolean activo;
}