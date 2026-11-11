package com.grupo10.epicentrogourmet.services.implementation;

import com.grupo10.epicentrogourmet.entities.Evaluacion;
import com.grupo10.epicentrogourmet.repositories.IEvaluacionRepository;
import com.grupo10.epicentrogourmet.services.IEvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class EvaluacionService implements IEvaluacionService {

    @Autowired
    private IEvaluacionRepository evaluacionRepository;
    public static final double UMBRAL_DESTACADO = 4.5;
    public static final double UMBRAL_OBSERVACION = 2.5;
    public static final double PLUS_DESTACADO = 15000;

    @Override
    public Evaluacion guardar(Evaluacion evaluacion) {
        // Validación: 1 evaluación por empleado por festival
        if (yaFueEvaluado(evaluacion.getEvaluado().getId(), evaluacion.getFestival().getId())) {
            throw new IllegalStateException("Este empleado ya fue evaluado en este festival");
        }


        double promedio = calcularPromedio(
                evaluacion.getPuntualidad(),
                evaluacion.getAtencionCliente(),
                evaluacion.getHigienePresentacion()
        );
        evaluacion.setPromedio(promedio);

        if (evaluacion.getFechaEvaluacion() == null) {
            evaluacion.setFechaEvaluacion(LocalDate.now());
        }

        return evaluacionRepository.save(evaluacion);
    }

    @Override
    public List<Evaluacion> listarPorFestival(Long festivalId) {
        return evaluacionRepository.findByFestivalIdOrderByPromedioDesc(festivalId);
    }

    @Override
    public List<Evaluacion> listarPorEmpleado(Long personalId) {
        return evaluacionRepository.findByEvaluadoId(personalId);
    }

    @Override
    public boolean yaFueEvaluado(Long personalId, Long festivalId) {
        return evaluacionRepository.existsByEvaluadoIdAndFestivalId(personalId, festivalId);
    }

    @Override
    public double calcularPromedio(int puntualidad, int atencionCliente, int higienePresentacion) {
        return (puntualidad + atencionCliente + higienePresentacion) / 3.0;
    }

    @Override
    public boolean esDestacado(double promedio) {
        return promedio >= UMBRAL_DESTACADO;
    }

    @Override
    public boolean esEnObservacion(double promedio) {
        return promedio < UMBRAL_OBSERVACION;
    }

    @Override
    public String getResultado(double promedio) {
        if (esDestacado(promedio)) return "DESTACADO ⭐";
        if (esEnObservacion(promedio)) return "EN OBSERVACIÓN ⚠️";
        return "SATISFACTORIO";
    }
    @Override
    public List<Evaluacion> getHistorialPorEmpleado(Long empleadoId) {
        return evaluacionRepository.findByEvaluadoIdOrderByFestivalFechaInicioAsc(empleadoId);
    }

    @Override
    public double getPromedioGeneral(Long empleadoId) {
        List<Evaluacion> lista = getHistorialPorEmpleado(empleadoId);
        if (lista.isEmpty()) return 0;
        return lista.stream().mapToDouble(Evaluacion::getPromedio).average().orElse(0);
    }




}