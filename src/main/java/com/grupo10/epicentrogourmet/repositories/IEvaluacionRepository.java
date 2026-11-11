package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IEvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    // Historial de un empleado
    List<Evaluacion> findByEvaluadoId(Long personalId);

    // Validación: 1 evaluación por empleado por festival
    boolean existsByEvaluadoIdAndFestivalId(Long personalId, Long festivalId);

    // ranking: mejores promedios primero
    List<Evaluacion> findByFestivalIdOrderByPromedioDesc(Long festivalId);
}
