package com.grupo10.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private ICostosRepository costosRepository;

    @Autowired
    private IUnidadVentaRepository unidadVentaRepository;

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

        long dias = 3;

        BigDecimal totalSup = BigDecimal.valueOf(uv.getSuperficieM2()).multiply(c.getCostoSuperficie());
        BigDecimal montaje = c.getCostoMontaje();
        boolean conElec = (uv instanceof FoodTruck ft) && ft.isRequiereConexionElectrica();
        BigDecimal elec = conElec ? c.getPlusElectricidad().multiply(BigDecimal.valueOf(dias)) : BigDecimal.ZERO;

        return DetalleCanonDTO.builder()
                .unidadId(uv.getId())
                .nombreComercial(uv.getNombreComercial())
                .superficieM2(uv.getSuperficieM2())
                .diasFestival(dias)
                .costoSuperficieTotal(totalSup)
                .costoMontaje(montaje)
                .costoElectricidad(elec)
                .totalCanon(totalSup.add(montaje).add(elec))
                .build();
    }
}