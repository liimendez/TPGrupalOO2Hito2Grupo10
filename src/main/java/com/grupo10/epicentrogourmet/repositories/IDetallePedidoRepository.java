package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("detallePedidoRepository")
public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Serializable> {}
