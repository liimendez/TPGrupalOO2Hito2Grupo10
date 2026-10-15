package com.grupo10.epicentrogourmet.dtos;

import com.grupo10.epicentrogourmet.entities.RoleType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserRoleDTO {
    private Long id;
    private RoleType rol;
}