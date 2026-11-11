package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.services.implementation.EvaluacionService;
import com.grupo10.epicentrogourmet.entities.Evaluacion;
import com.grupo10.epicentrogourmet.entities.Festival;
import com.grupo10.epicentrogourmet.entities.Personal;
import com.grupo10.epicentrogourmet.repositories.IFestivalRepository;
import com.grupo10.epicentrogourmet.repositories.IPersonalRepository;
import com.grupo10.epicentrogourmet.services.IEvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/responsable")
public class EvaluacionController {

    @Autowired
    private IEvaluacionService evaluacionService;

    @Autowired
    private IFestivalRepository festivalRepository;

    @Autowired
    private IPersonalRepository personalRepository;


    @GetMapping("/evaluar/{festivalId}")
    public String mostrarFormulario(@PathVariable Long festivalId, Model model, Authentication auth) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));

        String correo = auth.getName();

        // Solo empleados de MIS unidades en este festival
        // 1. Que la unidad este en el festival
        // 2. Que la unidad sea del responsable logueado
        List<Personal> empleados = personalRepository.findAll().stream()
                .filter(p -> p.getUnidadAsignada() != null
                        && festival.getUnidadesVenta().contains(p.getUnidadAsignada())
                        && p.getUnidadAsignada().getResponsable() != null
                        && correo.equals(p.getUnidadAsignada().getResponsable().getCorreo()))
                .toList();

        model.addAttribute("festival", festival);
        model.addAttribute("empleados", empleados);
        model.addAttribute("evaluacion", new Evaluacion());
        model.addAttribute("umbralDestacado", EvaluacionService.UMBRAL_DESTACADO);
        model.addAttribute("umbralObservacion", EvaluacionService.UMBRAL_OBSERVACION);
        model.addAttribute("plusDestacado", EvaluacionService.PLUS_DESTACADO);

        // Evaluaciones ya hechas en este festival, solo de MIS empleados
        List<Evaluacion> evaluaciones = evaluacionService.listarPorFestival(festivalId).stream()
                .filter(e -> e.getEvaluado() != null
                        && e.getEvaluado().getUnidadAsignada() != null
                        && e.getEvaluado().getUnidadAsignada().getResponsable() != null
                        && correo.equals(e.getEvaluado().getUnidadAsignada().getResponsable().getCorreo()))
                .toList();
        model.addAttribute("evaluaciones", evaluaciones);
        return "responsable/evaluar";
    }

    @PostMapping("/evaluar/guardar")
    public String guardar(@ModelAttribute Evaluacion evaluacion,
                          @RequestParam Long festivalId,
                          @RequestParam Long personalId,
                          RedirectAttributes redirect) {
        try {
            Festival festival = festivalRepository.findById(festivalId)
                    .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));
            Personal empleado = personalRepository.findById(personalId)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

            evaluacion.setFestival(festival);
            evaluacion.setEvaluado(empleado);

            evaluacionService.guardar(evaluacion);
            redirect.addFlashAttribute("exito", "Evaluación guardada: " + empleado.getNombre() + " " + empleado.getApellido());
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/responsable/evaluar/" + festivalId;
    }

    @GetMapping("/evaluaciones/{festivalId}")
    public String listar(@PathVariable Long festivalId, Model model, Authentication auth) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));

        String correo = auth.getName();
        List<Evaluacion> evaluaciones = evaluacionService.listarPorFestival(festivalId).stream()
                .filter(e -> e.getEvaluado() != null
                        && e.getEvaluado().getUnidadAsignada() != null
                        && e.getEvaluado().getUnidadAsignada().getResponsable() != null
                        && correo.equals(e.getEvaluado().getUnidadAsignada().getResponsable().getCorreo()))
                .toList();

        model.addAttribute("festival", festival);
        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("umbralDestacado", EvaluacionService.UMBRAL_DESTACADO);
        model.addAttribute("umbralObservacion", EvaluacionService.UMBRAL_OBSERVACION);
        model.addAttribute("plusDestacado", EvaluacionService.PLUS_DESTACADO);
        return "responsable/evaluaciones";
    }

    // cu - desempeño-historico // liimendez
    @GetMapping("/desempeno-historico")
    public String verHistorial(@RequestParam Long empleadoId, Model model, Authentication auth) {
        // 1. Buscar empleado
        Personal empleado = personalRepository.findById(empleadoId)
                .orElse(null);
        if (empleado == null) {
            return "redirect:/responsable/unidades";
        }

        // 2. Seguridad: solo empleados de SU unidad
        String correo = auth.getName();
        boolean esMio = empleado.getUnidadAsignada() != null
                && empleado.getUnidadAsignada().getResponsable() != null
                && correo.equals(empleado.getUnidadAsignada().getResponsable().getCorreo());
        if (!esMio) {
            return "redirect:/responsable/unidades";
        }

        // 3. Traer historial
        List<Evaluacion> historial = evaluacionService.getHistorialPorEmpleado(empleadoId);
        double promedioGeneral = evaluacionService.getPromedioGeneral(empleadoId);

        // 4. Si no tiene evaluaciones
        if (historial.isEmpty()) {
            model.addAttribute("mensaje", "Este empleado aún no tiene evaluaciones.");
        }

        // 5. Mejor y peor festival
        Evaluacion mejor = historial.stream()
                .max(Comparator.comparingDouble(Evaluacion::getPromedio)).orElse(null);
        Evaluacion peor = historial.stream()
                .min(Comparator.comparingDouble(Evaluacion::getPromedio)).orElse(null);

        model.addAttribute("empleado", empleado);
        model.addAttribute("historial", historial);
        model.addAttribute("promedioGeneral", promedioGeneral);
        model.addAttribute("cantidad", historial.size());
        model.addAttribute("mejor", mejor);
        model.addAttribute("peor", peor);
        model.addAttribute("umbralDestacado", EvaluacionService.UMBRAL_DESTACADO);
        model.addAttribute("umbralObservacion", EvaluacionService.UMBRAL_OBSERVACION);
        model.addAttribute("plusDestacado", EvaluacionService.PLUS_DESTACADO);

        return "responsable/desempeno-historico";
    }









}