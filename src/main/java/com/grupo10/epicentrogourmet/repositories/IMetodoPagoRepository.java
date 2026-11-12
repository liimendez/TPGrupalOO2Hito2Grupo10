package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository("metodoPagoRepository")
public interface IMetodoPagoRepository extends JpaRepository<MetodoPago, Serializable> {

    // Validación de unicidad ignorando mayúsculas/minúsculas
    boolean existsByNombreIgnoreCase(String nombre);

    // Listar solo los Metodos de Pago activos
    List<MetodoPago> findByActivoTrue();
}