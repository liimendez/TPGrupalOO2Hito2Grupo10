package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface IPersonalRepository extends JpaRepository<Personal, Long> {

    @Query("SELECT p FROM Personal p WHERE p.unidadAsignada.id = :idUnidad AND p.activo = true")
    List<Personal> findByUnidadVentaId(Long idUnidad);

    @Query("SELECT p FROM Personal p WHERE p.unidadAsignada.id = :idUnidad AND p.activo = true")
    List<Personal> findByUnidadVentaIdAndActivoTrue(Long idUnidad);

    List<Personal> findByActivoTrue();
    Optional<Personal> findByDni(String dni);
    Optional<Personal> findByCredencialesCorreo(String correo);

    @Query("SELECT p FROM Personal p WHERE p.id = :id")
    Optional<Personal> findByIdIncludingInactive(Long id);

    @Query(value = "SELECT p.id, p.nombre, p.apellido, p.dni, p.activo, p.unidad_asignada_id, u.correo " +
            "FROM personal p LEFT JOIN usuario u ON p.usuario_id = u.id " +
            "WHERE p.unidad_asignada_id = :idUnidad", nativeQuery = true)
    List<Object[]> findRawByUnidad(Long idUnidad);

    @Query(value = "SELECT p.id, p.nombre, p.apellido, p.dni, p.activo, p.unidad_asignada_id, u.correo " +
            "FROM personal p LEFT JOIN usuario u ON p.usuario_id = u.id " +
            "WHERE p.unidad_asignada_id = :idUnidad", nativeQuery = true)
    List<Object[]> findRawByUnidadAndActivoTrue(Long idUnidad);

    @Query("SELECT p FROM Personal p WHERE p.credenciales.correo = :correo")
    Optional<Personal> findByCredencialesCorreoOptional(String correo);
}