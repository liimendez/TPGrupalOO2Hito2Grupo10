package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ayudante")
@Getter @Setter
public class Ayudante extends Personal {
    public Ayudante() {
        super();
        this.setTipoPersonal(PersonalType.AYUDANTE);
    }
}