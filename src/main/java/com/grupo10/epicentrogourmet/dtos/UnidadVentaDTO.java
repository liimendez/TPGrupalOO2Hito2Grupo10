package com.grupo10.epicentrogourmet.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnidadVentaDTO {
    private Long id;
    private String nombreComercial;
    private double superficieM2;
    private String codigoUnico;
    private String tipoUnidad; // "FOODTRUCK" o "PUESTODESARMABLE"
    private Long responsableId;
    private String responsableUsername;

    // Campos de FoodTruck
    private String patente;
    private Boolean requiereConexionElectrica;

    // Campos de PuestoDesarmable
    private Integer cantidadCarpas;
    private Integer tiempoMontajeMin;
}