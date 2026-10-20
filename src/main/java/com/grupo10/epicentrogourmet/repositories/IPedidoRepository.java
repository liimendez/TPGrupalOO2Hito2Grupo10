package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Pedido;
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


    @Query("SELECT p FROM Pedido p WHERE p.unidadVenta.id IN (SELECT uv.id FROM UnidadVenta uv JOIN uv.festivales f WHERE f.id = :festivalId)")
    List<Pedido> findByFestivalId(@Param("festivalId") Long festivalId);

    @Query("SELECT p FROM Pedido p JOIN p.unidadVenta uv JOIN uv.festivales f WHERE f.id = :festivalId AND uv.id = :unidadVentaId")
    List<Pedido> findByFestivalIdAndUnidadVentaId(@Param("festivalId") Long festivalId, @Param("unidadVentaId") Long unidadVentaId);
}