package com.grupo10.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.dtos.DetalleCanonDTO;
import com.grupo10.epicentrogourmet.entities.Costos;
import com.grupo10.epicentrogourmet.entities.FoodTruck;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.ICostosRepository;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.services.ICostosService;

@Service("costosService")
public class CostosService implements ICostosService {

    @Autowired private ICostosRepository costosRepository;
    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @PersistenceContext private EntityManager em;

    @Override public List<Costos> getAll() { return costosRepository.findAll(); }
    @Override public Optional<Costos> findById(Long id) { return costosRepository.findById(id); }
    @Override public Costos insertOrUpdate(Costos costos) { return costosRepository.save(costos); }
    @Override public boolean remove(Long id) {
        if (costosRepository.existsById(id)) { costosRepository.deleteById(id); return true; }
        return false;
    }

    @Override
    public DetalleCanonDTO calcularCanon(Long unidadId) {
        UnidadVenta uv = unidadVentaRepository.findById(unidadId)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada " + unidadId));
        Costos c = costosRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Cargá tabla costos"));

        // CONSULTA DIRECTA A LA BD - DEFINITIVO QUIEN ES FOODTRUCK
        Long esFoodTruckCount = ((Number) em.createNativeQuery("SELECT COUNT(*) FROM food_truck WHERE id = :id")
                .setParameter("id", unidadId).getSingleResult()).longValue();
        boolean esFoodTruck = esFoodTruckCount > 0;

        BigDecimal costoM2 = c.getCostoSuperficie() != null ? c.getCostoSuperficie() : new BigDecimal("500");
        BigDecimal costoElecFijo = c.getPlusElectricidad() != null ? c.getPlusElectricidad() : new BigDecimal("2000");
        BigDecimal costoMontajeHora = c.getCostoMontaje() != null ? c.getCostoMontaje() : new BigDecimal("10");

        Double supObj = uv.getSuperficieM2();
        double sup = (supObj != null && supObj > 0) ? supObj : 10.0;
        BigDecimal totalSup = BigDecimal.valueOf(sup).multiply(costoM2);

        BigDecimal costoElecFinal = BigDecimal.ZERO;
        BigDecimal costoMontajeFinal = BigDecimal.ZERO;
        BigDecimal totalCanon;

        if (esFoodTruck) {
            // leer si requiere eléctrica directo de la tabla
            Object reqElec = em.createNativeQuery("SELECT requiere_conexion_electrica FROM food_truck WHERE id = :id")
                    .setParameter("id", unidadId).getSingleResult();
            boolean conElec = false;
            if (reqElec instanceof Number n) conElec = n.intValue() == 1;
            else if (reqElec instanceof Boolean b) conElec = b;
            costoElecFinal = conElec ? costoElecFijo : BigDecimal.ZERO;
            totalCanon = totalSup.add(costoElecFinal);
        } else {
            Long tiempo = 3L;
            try {
                Object t = em.createNativeQuery("SELECT tiempo_montaje_min FROM puesto_desarmable WHERE id = :id")
                        .setParameter("id", unidadId).getSingleResult();
                if (t instanceof Number n) tiempo = n.longValue();
            } catch(Exception ignored){}
            costoMontajeFinal = costoMontajeHora.multiply(BigDecimal.valueOf(tiempo));
            totalCanon = totalSup.subtract(costoMontajeFinal);
        }

        return DetalleCanonDTO.builder()
                .unidadId(uv.getId())
                .nombreComercial(uv.getNombreComercial())
                .superficieM2(sup)
                .diasFestival(0L)
                .costoSuperficieTotal(totalSup)
                .costoMontaje(costoMontajeFinal)
                .costoElectricidad(costoElecFinal)
                .totalCanon(totalCanon)
                .build();
    }
}