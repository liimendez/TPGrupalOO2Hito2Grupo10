package com.grupo10.epicentrogourmet.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReporteMayoresCanonDTO {
    private Long unidadVentaId;
    private String nombreComercial;
    private String codigoUnico;
    private String tipoUnidad; // FoodTruck o Puesto
    private double canon;
}