package com.grupo10.epicentrogourmet.repositories;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.grupo10.epicentrogourmet.entities.Personal;

@Repository("personalRepository")
public interface IPersonalRepository extends JpaRepository<Personal, Serializable> {

    Optional<Personal> findByDni(String dni);

    @Query("SELECT p FROM Personal p WHERE p.unidadAsignada.id = :idUnidad")
    List<Personal> findByUnidadVentaId(@Param("idUnidad") Long idUnidad);
}