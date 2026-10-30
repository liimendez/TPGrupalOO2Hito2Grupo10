package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.CategoriaCocina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ICategoriaCocinaRepository extends JpaRepository<CategoriaCocina, Long> {
    Optional<CategoriaCocina> findByNombreIgnoreCase(String nombre);
}