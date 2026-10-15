package com.grupo10.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.dtos.PersonalDTO;
import com.grupo10.epicentrogourmet.entities.Personal;
import com.grupo10.epicentrogourmet.repositories.IPersonalRepository;
import com.grupo10.epicentrogourmet.services.IPersonalService;

@Service("personalService")
public class PersonalService implements IPersonalService {

    @Autowired @Qualifier("personalRepository")
    private IPersonalRepository personalRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override public List<Personal> getAll() { return personalRepository.findAll(); }
    @Override public Optional<Personal> findById(Long id) { return personalRepository.findById(id); }
    @Override public Personal findByDni(String dni) { return personalRepository.findByDni(dni).orElse(null); }

    @Override
    public List<PersonalDTO> findByUnidadVentaId(Long idUnidad) {
        List<Personal> lista = personalRepository.findByUnidadVentaId(idUnidad);
        return lista.stream()
                .map(p -> modelMapper.map(p, PersonalDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Personal insertOrUpdate(Personal personal) {
        if (personal.getFechaNacimiento() == null) {
            throw new RuntimeException("La fecha de nacimiento es obligatoria");
        }
        int edad = Period.between(personal.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) {
            throw new RuntimeException("El personal debe ser mayor de edad. Edad actual: " + edad);
        }
        if (personal.getDni() == null || personal.getDni().length() != 8) {
            throw new RuntimeException("DNI debe tener 8 dígitos");
        }
        return personalRepository.save(personal);
    }

    @Override public boolean remove(Long id) { try{ personalRepository.deleteById(id); return true; } catch(Exception e){ return false; } }
}