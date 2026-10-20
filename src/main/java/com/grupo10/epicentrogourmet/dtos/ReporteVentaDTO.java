package com.grupo10.epicentrogourmet.dtos;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ReporteVentaDTO {
    private Long festivalId;
    private String nombreFestival;
    private double recaudacionTotal; // suma de todos los pedidos
    private long cantidadPedidos;
}