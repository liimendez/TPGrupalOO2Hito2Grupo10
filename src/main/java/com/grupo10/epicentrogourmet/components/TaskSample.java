package com.grupo10.epicentrogourmet.components;

import org.springframework.stereotype.Component;

@Component
public class TaskSample {

    // Este componente se carga al iniciar la app
    // Por ahora lo dejamos vacío, solo para cumplir la capa components
    public TaskSample() {
        System.out.println("Componente TaskSample cargado - Epicentro Gourmet Grupo 10");
    }

    /*
    // Si quieren probar una tarea programada después:
    @Scheduled(fixedDelay=5000)
    public void runJob() {
        System.out.println("Hello! - Revisando pedidos");
    }
    */
}