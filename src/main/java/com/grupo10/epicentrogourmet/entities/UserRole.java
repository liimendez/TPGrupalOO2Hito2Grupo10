package com.grupo10.epicentrogourmet.entities;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor

public class UserRole {
    @Id @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole rol; // ADMIN,responsable, empleado, etc
}