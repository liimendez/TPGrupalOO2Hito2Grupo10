package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.Personal;
import com.grupo10.epicentrogourmet.dtos.PersonalDTO;

public interface IPersonalService {
    List<Personal> getAll();
    Optional<Personal> findById(Long id);
    Personal findByDni(String dni);
    Personal insertOrUpdate(Personal personal);
    boolean remove(Long id);
    List<PersonalDTO> findByUnidadVentaId(Long idUnidad);

    // LOGICA
    int getEdad(Long idPersonal);
    boolean esMayorDeEdad(Long idPersonal);
    int getAntiguedadAnios(Long idPersonal);
    long getAntiguedadMeses(Long idPersonal);

    Personal altaConUsuario(String tipo, PersonalDTO dto, Long unidadId);
    Personal reactivar(Long id);


}