package com.grupo10.epicentrogourmet.dtos;

import com.grupo10.epicentrogourmet.entities.RoleType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestUserRoleDTO {
    private RoleType rol;
}