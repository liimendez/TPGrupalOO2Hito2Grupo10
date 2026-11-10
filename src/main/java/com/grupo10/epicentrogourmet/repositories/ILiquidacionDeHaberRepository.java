package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.LiquidacionDeHaber;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ILiquidacionDeHaberRepository extends JpaRepository<LiquidacionDeHaber, Long> {

    List<LiquidacionDeHaber> findByPersonalId(Long personalId);

    List<LiquidacionDeHaber> findByPersonalIdOrderByFechaLiquidacionDesc(Long personalId);

    // se usa en festivalService punto 6 y 7
    @Query("SELECT l FROM LiquidacionDeHaber l JOIN l.unidadVenta uv JOIN uv.festivales f WHERE uv.id = :unidadVentaId AND f.id = :festivalId")
    List<LiquidacionDeHaber> findByFestivalIdAndUnidadVentaId(@Param("festivalId") Long festivalId, @Param("unidadVentaId") Long unidadVentaId);

    boolean existsByPersonal_IdAndFechaLiquidacion(Long personalId, LocalDate fechaLiquidacion);

    // NUEVO - validación 1 liquidación por empleado por festival
    // Spring Data lo resuelve solo por el nombre: personal.id + fechaLiquidacion BETWEEN desde AND hasta
    boolean existsByPersonal_IdAndFechaLiquidacionBetween(Long personalId, LocalDate desde, LocalDate hasta);

    @Query("SELECT DISTINCT l FROM LiquidacionDeHaber l WHERE l.unidadVenta = :unidadVenta AND l.fechaLiquidacion BETWEEN :inicio AND :fin")
    List<LiquidacionDeHaber> findLiquidacionesParaRentabilidad(@Param("unidadVenta") UnidadVenta unidadVenta, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}