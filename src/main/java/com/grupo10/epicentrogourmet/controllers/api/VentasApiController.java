package com.grupo10.epicentrogourmet.controllers.api;

import com.grupo10.epicentrogourmet.dto.VentaDiariaDTO;
import com.grupo10.epicentrogourmet.dtos.EvaluacionApiDTO;
import com.grupo10.epicentrogourmet.entities.Evaluacion;
import com.grupo10.epicentrogourmet.entities.Festival;
import com.grupo10.epicentrogourmet.entities.Personal;
import com.grupo10.epicentrogourmet.repositories.IFestivalRepository;
import com.grupo10.epicentrogourmet.repositories.IPersonalRepository;
import com.grupo10.epicentrogourmet.services.IEvaluacionService;
import com.grupo10.epicentrogourmet.services.IHistorialVentasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Autowired
    private IFestivalRepository festivalRepository;

    @Autowired
    private IPersonalRepository personalRepository;

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
    public ResponseEntity<?> crearEvaluacion(@RequestBody EvaluacionApiDTO dto) {
        Festival festival = festivalRepository.findById(dto.getFestivalId())
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));
        Personal empleado = personalRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setFestival(festival);
        evaluacion.setEvaluado(empleado);
        evaluacion.setPuntualidad(dto.getPuntualidad());
        evaluacion.setAtencionCliente(dto.getAtencionCliente());
        evaluacion.setHigienePresentacion(dto.getHigienePresentacion());
        evaluacion.setObservacion(dto.getObservacion());
        evaluacion.setFechaEvaluacion(LocalDate.now());

        evaluacionService.guardar(evaluacion);

        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Evaluacion creada",
                "empleado", empleado.getNombre() + " " + empleado.getApellido(),
                "festivalId", festival.getId(),
                "promedio", evaluacion.getPromedio()
        ));
    }
}