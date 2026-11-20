package com.grupo10.epicentrogourmet.controllers.api;

import com.grupo10.epicentrogourmet.dto.VentaDiariaDTO;
import com.grupo10.epicentrogourmet.dtos.EvaluacionApiDTO;
import com.grupo10.epicentrogourmet.entities.Evaluacion;
import com.grupo10.epicentrogourmet.services.IEvaluacionService;
import com.grupo10.epicentrogourmet.services.IHistorialVentasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas API - Livia Mendez", description = "Hito 3 - API REST")
public class VentasApiController {

    @Autowired
    private IHistorialVentasService historialVentasService;

    @Autowired
    private IEvaluacionService evaluacionService;

    @Operation(summary = "GET - Historial de ventas filtrado - Livia Mendez",
            description = "Filtra por festival, unidad, desde y hasta. Minimo 3 parametros exigidos.")
    @GetMapping("/historial")
    public ResponseEntity<?> historial(
            @Parameter(description = "Id del festival") @RequestParam Long festivalId,
            @Parameter(description = "Id de la unidad de venta") @RequestParam Long unidadId,
            @Parameter(description = "Desde YYYY-MM-DD") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Hasta YYYY-MM-DD") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        List<VentaDiariaDTO> historial = historialVentasService.obtenerHistorial(festivalId, unidadId, desde, hasta);
        double totalGeneral = historial.stream().mapToDouble(VentaDiariaDTO::getTotalDia).sum();
        int totalPedidos = historial.stream().mapToInt(VentaDiariaDTO::getCantidadPedidos).sum();

        return ResponseEntity.ok(Map.of(
                "festivalId", festivalId,
                "unidadId", unidadId,
                "desde", desde == null ? "" : desde.toString(),
                "hasta", hasta == null ? "" : hasta.toString(),
                "totalGeneral", totalGeneral,
                "totalPedidos", totalPedidos,
                "historial", historial
        ));
    }

    @Operation(summary = "POST - Crear evaluacion de empleado - Livia Mendez")
    @PostMapping("/evaluaciones")
    public ResponseEntity<?> crearEvaluacion(@Valid @RequestBody EvaluacionApiDTO dto) {
        try {
            Evaluacion evaluacion = evaluacionService.crearDesdeDTO(dto);
            return ResponseEntity.status(201).body(Map.of(
                    "mensaje", "Evaluacion creada",
                    "empleado", evaluacion.getEvaluado().getNombre() + " " + evaluacion.getEvaluado().getApellido(),
                    "festivalId", evaluacion.getFestival().getId(),
                    "promedio", evaluacion.getPromedio()
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "PUT - Actualizar evaluacion - Livia Mendez")
    @PutMapping("/evaluaciones/{id}")
    public ResponseEntity<?> actualizarEvaluacion(@PathVariable Long id, @Valid @RequestBody EvaluacionApiDTO dto) {
        try {
            Evaluacion evaluacion = evaluacionService.actualizar(id, dto);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Evaluacion actualizada",
                    "id", evaluacion.getId(),
                    "promedio", evaluacion.getPromedio()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "DELETE - Eliminar evaluacion - Livia Mendez")
    @DeleteMapping("/evaluaciones/{id}")
    public ResponseEntity<?> eliminarEvaluacion(@PathVariable Long id) {
        try {
            evaluacionService.eliminar(id);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Evaluacion eliminada",
                    "id", id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }





}