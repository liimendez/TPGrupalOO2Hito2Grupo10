package com.grupo10.epicentrogourmet.dtos;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RentabilidadNetaDTO {
    private Long festivalId;
    private Long unidadVentaId;
    private double gananciaBruta; // pedidos - costos
    private double totalSueldos;
    private double canon;
    private double rentabilidadNeta; // gananciaBruta - sueldos - canon
}