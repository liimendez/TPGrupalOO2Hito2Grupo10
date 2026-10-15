package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Serializable> {
    public abstract Optional<Pedido> findById(Long id);
    public abstract List<Pedido> findByUnidadVentaId(Long unidadVentaId);
}