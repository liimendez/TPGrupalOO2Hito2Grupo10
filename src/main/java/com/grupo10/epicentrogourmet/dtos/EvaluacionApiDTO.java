package com.grupo10.epicentrogourmet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public class EvaluacionApiDTO {

    @Schema(description = "Id del empleado a evaluar", example = "20")
    public Long empleadoId;

    @Schema(description = "Id del festival", example = "13")
    public Long festivalId;

    @Schema(description = "Puntualidad 1-10", example = "9")
    public Integer puntualidad;

    @Schema(description = "Atencion al cliente 1-10", example = "10")
    public Integer atencionCliente;

    @Schema(description = "Higiene 1-10", example = "8")
    public Integer higiene;

    // Getters y setters
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public Long getFestivalId() { return festivalId; }
    public void setFestivalId(Long festivalId) { this.festivalId = festivalId; }
    public Integer getPuntualidad() { return puntualidad; }
    public void setPuntualidad(Integer puntualidad) { this.puntualidad = puntualidad; }
    public Integer getAtencionCliente() { return atencionCliente; }
    public void setAtencionCliente(Integer atencionCliente) { this.atencionCliente = atencionCliente; }
    public Integer getHigiene() { return higiene; }
    public void setHigiene(Integer higiene) { this.higiene = higiene; }
}
