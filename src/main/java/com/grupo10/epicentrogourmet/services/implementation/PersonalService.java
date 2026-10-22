package com.grupo10.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.dtos.PersonalDTO;
import com.grupo10.epicentrogourmet.repositories.*;
import com.grupo10.epicentrogourmet.services.IPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("personalService")
public class PersonalService implements IPersonalService {

    @Autowired private IPersonalRepository repo;
    @Autowired private IUsuarioRepository usuarioRepository;
    @Autowired private IUserRoleRepository userRoleRepository;
    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    @Override public List<Personal> getAll() { return repo.findAll(); }
    @Override public Optional<Personal> findById(Long id) { return repo.findById(id); }
    @Override public Personal findByDni(String dni) { return repo.findByDni(dni).orElse(null); }
    @Override public Personal insertOrUpdate(Personal p) { return repo.save(p); }
    @Override public boolean remove(Long id) { if(repo.existsById(id)){ repo.deleteById(id); return true; } return false; }

    @Override public List<PersonalDTO> findByUnidadVentaId(Long idUnidad) {
        return repo.findByUnidadVentaId(idUnidad).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override public int getEdad(Long id) { Personal p = repo.findById(id).orElse(null); if(p==null||p.getFechaNacimiento()==null) return 0; return Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears(); }
    @Override public boolean esMayorDeEdad(Long id) { return getEdad(id) >= 18; }
    @Override public int getAntiguedadAnios(Long id) { Personal p = repo.findById(id).orElse(null); if(p==null||p.getFechaIngreso()==null) return 0; return Period.between(p.getFechaIngreso(), LocalDate.now()).getYears(); }
    @Override public long getAntiguedadMeses(Long id) { Personal p = repo.findById(id).orElse(null); if(p==null||p.getFechaIngreso()==null) return 0; return ChronoUnit.MONTHS.between(p.getFechaIngreso(), LocalDate.now()); }

    private PersonalDTO convertToDTO(Personal p) {
        PersonalDTO dto = new PersonalDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setApellido(p.getApellido());
        dto.setDni(p.getDni());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setFechaIngreso(p.getFechaIngreso());
        dto.setActivo(p.isActivo());
        if (p.getUnidadAsignada() != null) dto.setUnidadAsignadaId(p.getUnidadAsignada().getId());
        String simpleName = p.getClass().getSimpleName().toUpperCase();
        if (simpleName.contains("CAJERO")) dto.setTipoPersonal("CAJERO");
        else if (simpleName.contains("COCINERO")) dto.setTipoPersonal("COCINERO");
        // ACA ESTABA EL ERROR 76 - ahora usa getCorreo()
        dto.setEmail(p.getCredenciales() != null ? p.getCredenciales().getCorreo() : null);
        dto.setNombreUnidadAsignada(p.getUnidadAsignada() != null ? p.getUnidadAsignada().getNombreComercial() : null);
        return dto;
    }

    @Transactional
    public Personal altaConUsuario(String tipo, PersonalDTO dto, Long unidadId) {
        String passPlana = UUID.randomUUID().toString().substring(0, 8);

        // Vos solo tenés EMPLEADO, RESPONSABLE y ADMIN, usamos EMPLEADO para ambos
        UserRole userRole = userRoleRepository.findByRol(RoleType.EMPLEADO)
                .orElseThrow(() -> new RuntimeException("No existe el rol EMPLEADO en la BD"));

        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(passPlana));
        usuario.setRol(userRole);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        Personal personal = tipo.equals("COCINERO") ? new Cocinero() : new Cajero();
        personal.setNombre(dto.getNombre());
        personal.setApellido(dto.getApellido());
        personal.setDni(dto.getDni());
        personal.setFechaNacimiento(dto.getFechaNacimiento());
        personal.setFechaIngreso(LocalDate.now());
        personal.setActivo(true);
        personal.setCredenciales(usuario);
        personal.setUnidadAsignada(unidadVentaRepository.findById(unidadId).orElseThrow());
        repo.save(personal);

        emailService.enviarMail(dto.getEmail(), "Epicentro - Acceso", "Usuario: " + dto.getEmail() + " Pass: " + passPlana);
        return personal;
    }

    public Personal reactivar(Long id) {
        Personal p = repo.findByIdIncludingInactive(id).orElseThrow();
        p.setActivo(true);
        return repo.save(p);
    }
}