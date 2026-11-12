package com.grupo10.epicentrogourmet.services.implementation;

import com.grupo10.epicentrogourmet.entities.MetodoPago;
import com.grupo10.epicentrogourmet.repositories.IMetodoPagoRepository;
import com.grupo10.epicentrogourmet.services.IMetodoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("metodoPagoService")
public class MetodoPagoService implements IMetodoPagoService {

    @Autowired
    private IMetodoPagoRepository metodoPagoRepository;

    @Override
    public List<MetodoPago> getAll() {
        return metodoPagoRepository.findAll();
    }

    @Override
    public List<MetodoPago> getActivos() {
        return metodoPagoRepository.findByActivoTrue();
    }

    @Override
    public Optional<MetodoPago> findById(Long id) {
        return metodoPagoRepository.findById(id);
    }

    @Override
    @Transactional
    public MetodoPago insertOrUpdate(MetodoPago metodoPago) {
        // Validación: Nombre obligatorio, completar campo obligatorio
        if (metodoPago.getNombre() == null || metodoPago.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del método de pago es obligatorio.");
        }

        String nombreLimpio = metodoPago.getNombre().trim();

        // Validación: Unicidad del nombre (evitar duplicados ignorando mayúsculas/minúsculas)
        if (metodoPago.getId() == null) {
            if (metodoPagoRepository.existsByNombreIgnoreCase(nombreLimpio)) {
                throw new IllegalArgumentException("Ya existe un método de pago registrado con ese nombre.");
            }
        } else {
            Optional<MetodoPago> existente = metodoPagoRepository.findById(metodoPago.getId());
            if (existente.isPresent() && !existente.get().getNombre().equalsIgnoreCase(nombreLimpio)) {
                if (metodoPagoRepository.existsByNombreIgnoreCase(nombreLimpio)) {
                    throw new IllegalArgumentException("Ya existe otro método de pago con ese nombre.");
                }
            }
        }

        // Validación: Rango del porcentaje de descuento (0% a 100%)
        if (metodoPago.getDescuentoPorcentaje() < 0 || metodoPago.getDescuentoPorcentaje() > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar comprendido entre 0% y 100%.");
        }

        metodoPago.setNombre(nombreLimpio);
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    @Transactional
    public boolean remove(Long id) {
        Optional<MetodoPago> metodoOpt = metodoPagoRepository.findById(id);
        if (metodoOpt.isPresent()) {
            MetodoPago metodo = metodoOpt.get();
            // Baja lógica
            metodo.setActivo(false);
            metodoPagoRepository.save(metodo);
            return true;
        }
        return false;
    }
}