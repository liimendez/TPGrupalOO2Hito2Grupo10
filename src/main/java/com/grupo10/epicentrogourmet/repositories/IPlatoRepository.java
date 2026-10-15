package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("platoRepository")
public interface IPlatoRepository extends JpaRepository<Plato, Serializable> {
    public abstract Optional<Plato> findById(Long id);
    public abstract List<Plato> findByUnidadVentaId(Long unidadVentaId);
}
