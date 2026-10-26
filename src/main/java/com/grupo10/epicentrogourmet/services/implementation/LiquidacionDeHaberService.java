package com.grupo10.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.repositories.*;
import com.grupo10.epicentrogourmet.services.ILiquidacionDeHaberService;
import com.grupo10.epicentrogourmet.services.IPersonalService;

@Service("liquidacionDeHaberService")
public class LiquidacionDeHaberService implements ILiquidacionDeHaberService {

    @Autowired private ILiquidacionDeHaberRepository liquidacionRepo;
    @Autowired private IPersonalRepository personalRepo;
    @Autowired private IUnidadVentaRepository unidadVentaRepo;
    @Autowired private ICostosRepository costosRepo;
    @Autowired private IPersonalService personalService;

    @Override public List<LiquidacionDeHaber> getAll() { return liquidacionRepo.findAll(); }
    @Override public Optional<LiquidacionDeHaber> findById(Long id) { return liquidacionRepo.findById(id); }
    @Override public List<LiquidacionDeHaber> findByPersonalId(Long pid) { return liquidacionRepo.findByPersonalId(pid); }
    @Override public LiquidacionDeHaber insertOrUpdate(LiquidacionDeHaber l) { return liquidacionRepo.save(l); }
    @Override public boolean remove(Long id) { if(liquidacionRepo.existsById(id)){ liquidacionRepo.deleteById(id); return true;} return false; }

    @Override
    public double calcularTotalACobrar(Long idPersonal, Long idUnidadVenta) {
        Personal p = personalRepo.findById(idPersonal).orElseThrow();
        Costos costos = costosRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Cargá tabla costos"));

        // DIAS del festival
        long dias = 3;
        try {
            UnidadVenta uv = unidadVentaRepo.findById(idUnidadVenta).orElse(null);
            if (uv != null && uv.getFestivales() != null && !uv.getFestivales().isEmpty()) {
                Festival fest = uv.getFestivales().iterator().next();
                if (fest.getFechaInicio() != null && fest.getFechaFin() != null) {
                    dias = ChronoUnit.DAYS.between(fest.getFechaInicio(), fest.getFechaFin());
                    if (dias <= 0) dias = 3;
                }
            }
        } catch(Exception e){ dias = 3; }

        int tramos = (int)(dias / 15) + 1;
        BigDecimal sueldoBase = costos.getSueldoBase() != null ? costos.getSueldoBase() : BigDecimal.valueOf(100000);

        if (p instanceof Cocinero c) {
            String cat = c.getCategoria() != null ? c.getCategoria().toUpperCase() : "LAVAPLATOS";
            BigDecimal plus;
            if (cat.equals("COCINERO")) plus = costos.getPlusCocinero() != null ? costos.getPlusCocinero() : BigDecimal.valueOf(150000);
            else if (cat.contains("AYUDANTE")) plus = costos.getPlusAyudante() != null ? costos.getPlusAyudante() : BigDecimal.valueOf(100000);
            else plus = costos.getPlusLavaplatos() != null ? costos.getPlusLavaplatos() : BigDecimal.valueOf(50000);

            // (1) totalACobrarCocina=(int(dias/15)+1) * 50% *(sueldoBase+plusCategoria) - SIN antigüedad
            return (sueldoBase.doubleValue() + plus.doubleValue()) * 0.5 * tramos;

        } else { // Cajero
            int anios = 0;
            try { anios = personalService.getAntiguedadAnios(idPersonal); } catch(Exception e){ anios = 0; }
            BigDecimal antiguedad = BigDecimal.valueOf(anios * 5000L);

            // (2) totalACobrarCajero=(int(dias/15)+1) * 50% *(sueldoBase+antigüedad) - SIN plus
            return (sueldoBase.doubleValue() + antiguedad.doubleValue()) * 0.5 * tramos;
        }
    }

    @Override
    public LiquidacionDeHaber liquidar(Long idPersonal, Long idUnidadVenta) {
        Personal p = personalRepo.findById(idPersonal).orElseThrow();
        UnidadVenta uv = unidadVentaRepo.findById(idUnidadVenta).orElseThrow();


        long dias = 30;
        if (uv.getFestivales() != null && !uv.getFestivales().isEmpty()) {
            Festival fest = uv.getFestivales().iterator().next();
            if (fest.getFechaInicio() != null && fest.getFechaFin() != null) {
                long diff = ChronoUnit.DAYS.between(fest.getFechaInicio(), fest.getFechaFin());
                if(diff > 0 && diff <= 30) dias = diff;
                else if(diff > 30) dias = 30;
            }
        }

        double total = calcularTotalACobrar(idPersonal, idUnidadVenta);

        LiquidacionDeHaber liq = new LiquidacionDeHaber();
        liq.setPersonal(p);
        liq.setUnidadVenta(uv);
        liq.setDiasDeTrabajo((int)dias);
        liq.setTotalACobrar(total);
        liq.setFechaLiquidacion(LocalDate.now());

        return liquidacionRepo.save(liq);
    }
}