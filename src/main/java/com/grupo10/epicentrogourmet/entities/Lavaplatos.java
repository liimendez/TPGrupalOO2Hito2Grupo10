package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lavaplatos")
@Getter @Setter
public class Lavaplatos extends Personal {
    public Lavaplatos() {
        super();
        this.setTipoPersonal(PersonalType.LAVAPLATOS);
    }
}