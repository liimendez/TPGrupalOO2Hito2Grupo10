package com.grupo10.epicentrogourmet.services;

import com.grupo10.epicentrogourmet.dtos.EvaluacionApiDTO;
import com.grupo10.epicentrogourmet.entities.Evaluacion;
import java.util.List;

public interface IEvaluacionService {

    Evaluacion guardar(Evaluacion evaluacion);

    Evaluacion crearDesdeDTO(EvaluacionApiDTO dto);

    Evaluacion actualizar(Long id, EvaluacionApiDTO dto);
    void eliminar(Long id);

    List<Evaluacion> listarPorFestival(Long festivalId);

    List<Evaluacion> listarPorEmpleado(Long personalId);

    boolean yaFueEvaluado(Long personalId, Long festivalId);

    double calcularPromedio(int puntualidad, int atencionCliente, int higienePresentacion);

    boolean esDestacado(double promedio);

    boolean esEnObservacion(double promedio);

    String getResultado(double promedio);

    List<Evaluacion> getHistorialPorEmpleado(Long empleadoId);
    double getPromedioGeneral(Long empleadoId);

}