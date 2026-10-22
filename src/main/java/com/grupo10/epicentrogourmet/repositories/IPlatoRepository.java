package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IPlatoRepository extends JpaRepository<Plato, Long> {
    List<Plato> findByUnidadVenta_Id(Long unidadVentaId);
}