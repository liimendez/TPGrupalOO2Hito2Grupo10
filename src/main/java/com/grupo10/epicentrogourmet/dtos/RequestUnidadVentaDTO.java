package com.grupo10.epicentrogourmet.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestUnidadVentaDTO {
    private String nombreComercial;
    private double superficieM2;
    private String codigoUnico;
    private Long responsableId;

    private String tipoUnidad; // FOODTRUCK / PUESTODESARMABLE

    // Solo si es FOODTRUCK
    private String patente;
    private boolean requiereConexionElectrica;

    // Solo si es PUESTODESARMABLE
    private int cantidadCarpas;
    private int tiempoMontajeMin;

    private String correo;
    private String password;
}