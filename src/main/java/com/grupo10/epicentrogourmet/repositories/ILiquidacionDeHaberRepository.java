package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.LiquidacionDeHaber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository("liquidacionDeHaberRepository")
public interface ILiquidacionDeHaberRepository extends JpaRepository<LiquidacionDeHaber, Serializable> {
    public abstract List<LiquidacionDeHaber> findByPersonalId(Long personalId);
}