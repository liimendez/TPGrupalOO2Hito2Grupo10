package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Costos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("costosRepository")
public interface ICostosRepository extends JpaRepository<Costos, Serializable> {}