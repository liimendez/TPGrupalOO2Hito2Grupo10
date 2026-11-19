package com.grupo10.epicentrogourmet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public class EvaluacionApiDTO {

    @Schema(description = "Id del empleado a evaluar", example = "20")
    public Long empleadoId;

    @Schema(description = "Id del festival", example = "13")
    public Long festivalId;

    @Schema(description = "Puntualidad 1-5", example = "4")
    public Integer puntualidad;

    @Schema(description = "Atencion al cliente 1-5", example = "5")
    public Integer atencionCliente;

    @Schema(description = "Higiene y presentacion 1-5", example = "4")
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