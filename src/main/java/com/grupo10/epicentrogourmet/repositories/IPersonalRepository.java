package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface IPersonalRepository extends JpaRepository<Personal, Long> {

    @Query("SELECT p FROM Personal p WHERE p.activo = true")
    List<Personal> findByActivoTrue();

    Optional<Personal> findByDni(String dni);

    @Query("SELECT p FROM Personal p WHERE p.unidadAsignada.id = :id AND p.activo = true")
    List<Personal> findByUnidadVentaIdAndActivoTrue(@Param("id") Long id);

    @Query("SELECT p.id, p.nombre, p.apellido, p.dni, p.activo, p.unidadAsignada.id, p.credenciales.correo FROM Personal p WHERE p.unidadAsignada.id = :id")
    List<Object[]> findRawByUnidad(@Param("id") Long id);

    // ESTE TRAE TODOS (activos + inactivos)
    @Query("SELECT p FROM Personal p LEFT JOIN FETCH p.credenciales LEFT JOIN FETCH p.unidadAsignada WHERE p.unidadAsignada.id = :id")
    List<Personal> findByUnidadVentaId(@Param("id") Long id);


    @Query("SELECT p FROM Personal p LEFT JOIN FETCH p.credenciales LEFT JOIN FETCH p.unidadAsignada WHERE p.credenciales.correo = :correo")
    Optional<Personal> findByCredencialesCorreo(@Param("correo") String correo);

    @Query("SELECT p FROM Personal p LEFT JOIN FETCH p.credenciales LEFT JOIN FETCH p.unidadAsignada WHERE p.id = :id")
    Optional<Personal> findByIdIncludingInactive(@Param("id") Long id);
}