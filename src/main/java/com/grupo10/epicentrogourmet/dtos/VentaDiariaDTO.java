package com.grupo10.epicentrogourmet.dto;

import java.time.LocalDate;

public class VentaDiariaDTO {

    private LocalDate fecha;
    private int cantidadPedidos;
    private double totalDia;

    public VentaDiariaDTO() {
    }

    public VentaDiariaDTO(LocalDate fecha, int cantidadPedidos, double totalDia) {
        this.fecha = fecha;
        this.cantidadPedidos = cantidadPedidos;
        this.totalDia = totalDia;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getCantidadPedidos() {
        return cantidadPedidos;
    }

    public void setCantidadPedidos(int cantidadPedidos) {
        this.cantidadPedidos = cantidadPedidos;
    }

    public double getTotalDia() {
        return totalDia;
    }

    public void setTotalDia(double totalDia) {
        this.totalDia = totalDia;
    }
}