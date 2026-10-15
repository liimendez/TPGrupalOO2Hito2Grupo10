package com.grupo10.epicentrogourmet.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestPlatoDTO {
    private String nombre;
    private double precioVenta;
    private double costoProduccion;
    private Long unidadVentaId;
}
