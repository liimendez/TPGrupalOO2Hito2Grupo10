package com.grupo10.epicentrogourmet.services.implementation;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.entities.Plato;
import com.grupo10.epicentrogourmet.repositories.IPlatoRepository;
import com.grupo10.epicentrogourmet.services.IPlatoService;

@Service("platoService")
public class PlatoService implements IPlatoService {

    @Autowired
    private IPlatoRepository repo;

    @Override
    public List<Plato> getAll() { return repo.findAll(); }

    @Override
    public Optional<Plato> findById(Long id) { return repo.findById(id); }

    @Override
    public Plato insertOrUpdate(Plato e) { return repo.save(e); }

    @Override
    public boolean remove(Long id) {
        if(repo.existsById(id)){ repo.deleteById(id); return true; }
        return false;
    }

    @Override
    public List<Plato> findByUnidadVentaId(Long idUnidad) {
        return repo.findByUnidadVentaId(idUnidad);
    }
}