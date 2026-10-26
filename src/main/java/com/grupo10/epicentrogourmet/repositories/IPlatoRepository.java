package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPlatoRepository extends JpaRepository<Plato, Long> {
    List<Plato> findByUnidadVenta_Id(Long unidadVentaId);

    @Query("SELECT p FROM Plato p LEFT JOIN FETCH p.unidadVenta WHERE p.id = :id")
    Optional<Plato> findByIdWithUnidad(@Param("id") Long id);
}