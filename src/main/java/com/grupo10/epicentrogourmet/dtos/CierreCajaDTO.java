package com.grupo10.epicentrogourmet.dtos;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CierreCajaDTO {
    private LocalDate fecha;
    private String nombreUnidad;
    private String nombreCajero;
    private double totalRecaudado;
    private int cantidadPedidos;
}