package com.grupo10.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.entities.Personal;
import com.grupo10.epicentrogourmet.dtos.PersonalDTO;
import com.grupo10.epicentrogourmet.repositories.IPersonalRepository;
import com.grupo10.epicentrogourmet.services.IPersonalService;

@Service("personalService")
public class PersonalService implements IPersonalService {

    @Autowired
    private IPersonalRepository repo;

    @Override public List<Personal> getAll() { return repo.findAll(); }
    @Override public Optional<Personal> findById(Long id) { return repo.findById(id); }
    @Override public Personal findByDni(String dni) { return repo.findByDni(dni).orElse(null); }
    @Override public Personal insertOrUpdate(Personal p) { return repo.save(p); }
    @Override public boolean remove(Long id) { if(repo.existsById(id)){ repo.deleteById(id); return true; } return false; }

    @Override
    public List<PersonalDTO> findByUnidadVentaId(Long idUnidad) {
        return repo.findByUnidadVentaId(idUnidad).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========= LOGICA DE NEGOCIO EN EL SERVICE =========

    @Override
    public int getEdad(Long idPersonal) {
        Personal p = repo.findById(idPersonal).orElse(null);
        if (p == null || p.getFechaNacimiento() == null) return 0;
        return Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears();
    }

    @Override
    public boolean esMayorDeEdad(Long idPersonal) {
        return getEdad(idPersonal) >= 18;
    }

    @Override
    public int getAntiguedadAnios(Long idPersonal) {
        Personal p = repo.findById(idPersonal).orElse(null);
        if (p == null || p.getFechaIngreso() == null) return 0;
        return Period.between(p.getFechaIngreso(), LocalDate.now()).getYears();
    }

    @Override
    public long getAntiguedadMeses(Long idPersonal) {
        Personal p = repo.findById(idPersonal).orElse(null);
        if (p == null || p.getFechaIngreso() == null) return 0;
        return ChronoUnit.MONTHS.between(p.getFechaIngreso(), LocalDate.now());
    }

    private PersonalDTO convertToDTO(Personal p) {
        PersonalDTO dto = new PersonalDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setApellido(p.getApellido());
        dto.setDni(p.getDni());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setFechaIngreso(p.getFechaIngreso());
        dto.setActivo(p.isActivo());
        if (p.getUnidadAsignada() != null) {
            dto.setUnidadAsignadaId(p.getUnidadAsignada().getId());
        }
        String simpleName = p.getClass().getSimpleName().toUpperCase();
        if (simpleName.contains("CAJERO")) dto.setTipoPersonal("CAJERO");
        else if (simpleName.contains("COCINERO")) dto.setTipoPersonal("COCINERO");
        return dto;
    }
}