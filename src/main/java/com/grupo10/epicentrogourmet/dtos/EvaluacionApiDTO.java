package com.grupo10.epicentrogourmet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EvaluacionApiDTO {

    @Schema(description = "Id del empleado a evaluar", example = "20")
    @NotNull(message = "empleadoId es obligatorio")
    public Long empleadoId;

    @Schema(description = "Id del festival", example = "13")
    @NotNull(message = "festivalId es obligatorio")
    public Long festivalId;

    @Schema(description = "Puntualidad 1-5", example = "4")
    @NotNull(message = "puntualidad es obligatoria")
    @Min(value = 1, message = "puntualidad debe ser entre 1 y 5")
    @Max(value = 5, message = "puntualidad debe ser entre 1 y 5")
    public Integer puntualidad;

    @Schema(description = "Atencion al cliente 1-5", example = "5")
    @NotNull(message = "atencionCliente es obligatoria")
    @Min(value = 1, message = "atencionCliente debe ser entre 1 y 5")
    @Max(value = 5, message = "atencionCliente debe ser entre 1 y 5")
    public Integer atencionCliente;

    @Schema(description = "Higiene y presentacion 1-5", example = "4")
    @NotNull(message = "higienePresentacion es obligatoria")
    @Min(value = 1, message = "higienePresentacion debe ser entre 1 y 5")
    @Max(value = 5, message = "higienePresentacion debe ser entre 1 y 5")
    public Integer higienePresentacion;

    @Schema(description = "Observacion opcional", example = "Muy buena atencion")
    public String observacion;

    // Getters y setters
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }

    public Long getFestivalId() { return festivalId; }
    public void setFestivalId(Long festivalId) { this.festivalId = festivalId; }

    public Integer getPuntualidad() { return puntualidad; }
    public void setPuntualidad(Integer puntualidad) { this.puntualidad = puntualidad; }

    public Integer getAtencionCliente() { return atencionCliente; }
    public void setAtencionCliente(Integer atencionCliente) { this.atencionCliente = atencionCliente; }

    public Integer getHigienePresentacion() { return higienePresentacion; }
    public void setHigienePresentacion(Integer higienePresentacion) { this.higienePresentacion = higienePresentacion; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}