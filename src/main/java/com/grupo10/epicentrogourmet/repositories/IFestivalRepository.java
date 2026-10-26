package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("festivalRepository")
public interface IFestivalRepository extends JpaRepository<Festival, Serializable> {
    public abstract Optional<Festival> findById(Long id);
    public abstract List<Festival> findByNombre(String nombre);
    public abstract List<Festival> findByTemporada(String temporada);
    @Query("SELECT f FROM Festival f LEFT JOIN FETCH f.unidadesVenta WHERE f.id = :id")
    Optional<Festival> findByIdWithUnidades(@Param("id") Long id);
}
