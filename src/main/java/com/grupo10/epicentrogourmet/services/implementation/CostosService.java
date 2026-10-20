package com.grupo10.epicentrogourmet.services.implementation;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.entities.Costos;
import com.grupo10.epicentrogourmet.repositories.ICostosRepository;
import com.grupo10.epicentrogourmet.services.ICostosService;

@Service("costosService")
public class CostosService implements ICostosService {

    @Autowired
    private ICostosRepository costosRepository;

    @Override
    public List<Costos> getAll() {
        return costosRepository.findAll();
    }

    @Override
    public Optional<Costos> findById(Long id) {
        return costosRepository.findById(id);
    }

    @Override
    public Costos insertOrUpdate(Costos costos) {
        return costosRepository.save(costos);
    }

    @Override
    public boolean remove(Long id) {
        if (costosRepository.existsById(id)) {
            costosRepository.deleteById(id);
            return true;
        }
        return false;
    }
}