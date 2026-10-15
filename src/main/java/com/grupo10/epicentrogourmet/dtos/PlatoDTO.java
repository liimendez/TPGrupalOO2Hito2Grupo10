package com.grupo10.epicentrogourmet.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlatoDTO {
    private Long id;
    private String nombre;
    private double precioVenta;
    private double costoProduccion;
    private Long unidadVentaId;
    private String nombreComercialUnidad;
}