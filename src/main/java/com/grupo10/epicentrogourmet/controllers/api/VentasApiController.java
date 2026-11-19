package com.grupo10.epicentrogourmet.controllers.api;

import com.grupo10.epicentrogourmet.dtos.EvaluacionApiDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas API - Livia Mendez", description = "Hito 3 - API REST")
public class VentasApiController {

    // aca inyecto el servicio real hito 2

    @Operation(summary = "GET - Historial de ventas filtrado - Livia Mendez",
            description = "Filtra por unidad, desde, hasta y metodo de pago. Minimo 3 parametros exigidos.")
    @GetMapping("/historial")
    public ResponseEntity<?> historial(
            @Parameter(description = "Id de la unidad de venta") @RequestParam Long unidadId,
            @Parameter(description = "Desde YYYY-MM-DD") @RequestParam String desde,
            @Parameter(description = "Hasta YYYY-MM-DD") @RequestParam String hasta,
            @Parameter(description = "Metodo de pago (opcional)") @RequestParam(required = false) String metodoPago
    ) {
        return ResponseEntity.ok(Map.of(
                "unidadId", unidadId,
                "desde", desde,
                "hasta", hasta,
                "metodoPago", metodoPago == null ? "TODOS" : metodoPago,
                "mensaje", "Aca va tu historial real del Hito 2"
        ));
    }

    @Operation(summary = "POST - Crear evaluacion de empleado - Livia Mendez")
    @PostMapping("/evaluaciones")
    public ResponseEntity<?> crearEvaluacion(@RequestBody EvaluacionApiDTO dto) {
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Evaluacion creada",
                "datos", dto
        ));
    }
}