package com.grupo10.epicentrogourmet.dtos;

import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestUsuarioDTO {
    private String correo;
    private String password;
    private Long rolId;
}
