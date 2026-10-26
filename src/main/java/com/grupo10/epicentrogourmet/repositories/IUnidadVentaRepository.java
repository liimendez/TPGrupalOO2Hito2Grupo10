package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("unidadVentaRepository")
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Serializable> {
    Optional<UnidadVenta> findById(Long id);
    Optional<UnidadVenta> findByCodigoUnico(String codigoUnico);
    Optional<UnidadVenta> findByNombreComercial(String nombreComercial);

    @Query("SELECT uv FROM UnidadVenta uv JOIN uv.festivales f WHERE f.id = :idFestival")
    List<UnidadVenta> findByFestivalId(Long idFestival);

    @Query("SELECT uv FROM UnidadVenta uv WHERE uv.responsable.correo = :correo")
    Optional<UnidadVenta> findMiUnidad(@Param("correo") String correo);

    @Query("SELECT uv FROM UnidadVenta uv LEFT JOIN FETCH uv.platosOfrecidos p WHERE uv.id = :id ORDER BY p.id ASC")
    Optional<UnidadVenta> findByIdWithPlatos(@Param("id") Long id);
}