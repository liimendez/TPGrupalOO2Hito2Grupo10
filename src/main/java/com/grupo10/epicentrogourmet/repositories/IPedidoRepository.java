package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Pedido;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Serializable> {

    Optional<Pedido> findById(Long id);
    List<Pedido> findByUnidadVentaId(Long unidadVentaId);

    // FIX para /pedido/unidad/1 - trae detalles + plato con FETCH para evitar LazyInitializationException 500
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.plato WHERE p.unidadVenta.id = :unidadVentaId")
    List<Pedido> findByUnidadVentaIdConDetalles(@Param("unidadVentaId") Long unidadVentaId);

    @Query("SELECT p FROM Pedido p WHERE p.unidadVenta.id IN (SELECT uv.id FROM UnidadVenta uv JOIN uv.festivales f WHERE f.id = :festivalId)")
    List<Pedido> findByFestivalId(@Param("festivalId") Long festivalId);

    @Query("SELECT p FROM Pedido p JOIN p.unidadVenta uv JOIN uv.festivales f WHERE f.id = :festivalId AND uv.id = :unidadVentaId")
    List<Pedido> findByFestivalIdAndUnidadVentaId(@Param("festivalId") Long festivalId, @Param("unidadVentaId") Long unidadVentaId);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.plato WHERE p.unidadVenta.id IN (SELECT uv.id FROM UnidadVenta uv JOIN uv.festivales f WHERE f.id = :festivalId)")
    List<Pedido> findByFestivalIdConDetalles(@Param("festivalId") Long festivalId);

    @Query("SELECT DISTINCT p FROM Pedido p " +
            "INNER JOIN FETCH p.unidadVenta uv " +
            "INNER JOIN FETCH p.detalles d " +
            "INNER JOIN FETCH d.plato pl " +
            "WHERE uv = :unidadVenta")
    List<Pedido> generarReportePorUnidadVenta(@Param("unidadVenta") UnidadVenta unidadVenta);
}