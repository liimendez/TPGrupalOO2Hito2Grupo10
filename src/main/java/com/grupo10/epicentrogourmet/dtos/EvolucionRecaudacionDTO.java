package com.grupo10.epicentrogourmet.dtos;

import java.time.LocalDate;

public class EvolucionRecaudacionDTO {
    private LocalDate fecha;
    private int cantidadPedidos;
    private double totalDia;
    private Double variacion;

    public EvolucionRecaudacionDTO(LocalDate fecha, int cantidadPedidos, double totalDia, Double variacion) {
        this.fecha = fecha;
        this.cantidadPedidos = cantidadPedidos;
        this.totalDia = totalDia;
        this.variacion = variacion;
    }

    public LocalDate getFecha() { return fecha; }
    public int getCantidadPedidos() { return cantidadPedidos; }
    public double getTotalDia() { return totalDia; }
    public Double getVariacion() { return variacion; }
}