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
        UnidadVenta uv = unidadVentaRepo.findById(idUnidadVenta).orElseThrow();
        Costos costos = costosRepo.findAll().stream().findFirst().orElseThrow();

        // FIX 1: UnidadVenta tiene Set<Festival> festivales
        long diasFest = 30;
        if (uv.getFestivales() != null && !uv.getFestivales().isEmpty()) {
            Festival fest = uv.getFestivales().iterator().next(); // tomamos el primero
            if (fest.getFechaInicio() != null && fest.getFechaFin() != null) {
                diasFest = ChronoUnit.DAYS.between(fest.getFechaInicio(), fest.getFechaFin());
                if (diasFest <= 0) diasFest = 30;
            }
        }

        int factor = (int)(diasFest / 15) + 1; // FORMULA QUE SE PIDE EN EL TP

        BigDecimal base;
        if (p instanceof Cocinero) {
            // FIX 2: categoria es String, no Enum
            Cocinero c = (Cocinero) p;
            String cat = c.getCategoria() != null ? c.getCategoria().toUpperCase() : "LAVAPLATOS";
            BigDecimal plus;
            if (cat.equals("COCINERO")) plus = costos.getPlusCocinero();
            else if (cat.contains("AYUDANTE")) plus = costos.getPlusAyudante();
            else plus = costos.getPlusLavaplatos(); // default lavaplatos

            base = costos.getSueldoBase().add(plus);
        } else {
            // CAJERO: sueldoBase + antigüedad * $5000
            int anios = personalService.getAntiguedadAnios(idPersonal);
            BigDecimal antiguedad = BigDecimal.valueOf(anios * 5000L);
            base = costos.getSueldoBase().add(antiguedad);
        }

        return base.doubleValue() * 0.5 * factor;
    }

    @Override
    public LiquidacionDeHaber liquidar(Long idPersonal, Long idUnidadVenta) {
        Personal p = personalRepo.findById(idPersonal).orElseThrow();
        UnidadVenta uv = unidadVentaRepo.findById(idUnidadVenta).orElseThrow();

        long dias = 30;
        if (uv.getFestivales() != null && !uv.getFestivales().isEmpty()) {
            Festival fest = uv.getFestivales().iterator().next();
            if (fest.getFechaInicio() != null && fest.getFechaFin() != null) {
                dias = ChronoUnit.DAYS.between(fest.getFechaInicio(), fest.getFechaFin());
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