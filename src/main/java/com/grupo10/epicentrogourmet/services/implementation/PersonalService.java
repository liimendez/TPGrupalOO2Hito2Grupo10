package com.grupo10.epicentrogourmet.services.implementation;

import com.grupo10.epicentrogourmet.dtos.PersonalDTO;
import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.repositories.IPersonalRepository;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.repositories.IUserRoleRepository;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;
import com.grupo10.epicentrogourmet.services.IPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("personalService")
public class PersonalService implements IPersonalService {

    @Autowired private IPersonalRepository repo;
    @Autowired private IUsuarioRepository usuarioRepository;
    @Autowired private IUserRoleRepository userRoleRepository;
    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    @Override public List<Personal> getAll() { return repo.findByActivoTrue(); }
    @Override public Optional<Personal> findById(Long id) { return repo.findById(id); }
    @Override public Personal findByDni(String dni) { return repo.findByDni(dni).orElse(null); }
    @Override public Personal insertOrUpdate(Personal p) { return repo.save(p); }

    @Override
    @Transactional
    public boolean remove(Long id) {
        Personal p = repo.findById(id).orElse(null);
        if(p != null){
            p.setActivo(false);
            if(p.getCredenciales() != null){
                p.getCredenciales().setActivo(false);
                usuarioRepository.save(p.getCredenciales());
            }
            repo.save(p);
            return true;
        }
        return false;
    }

    @Override
    public List<PersonalDTO> findByUnidadVentaId(Long idUnidad) {
        try {
            return repo.findByUnidadVentaIdAndActivoTrue(idUnidad).stream()
                    .map(this::convertToDTO)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<PersonalDTO> findByUnidadVentaIdTodos(Long idUnidad) {
        try {
            List<Object[]> rows = repo.findRawByUnidad(idUnidad);
            String nombreUnidad = unidadVentaRepository.findById(idUnidad)
                    .map(u -> u.getNombreComercial())
                    .orElse("Unidad " + idUnidad);

            return rows.stream().map(r -> {
                PersonalDTO dto = new PersonalDTO();
                dto.setId(((Number) r[0]).longValue());
                dto.setNombre((String) r[1]);
                dto.setApellido((String) r[2]);
                dto.setDni(r[3] != null ? r[3].toString() : "");
                dto.setActivo(r[4] instanceof Boolean ? (Boolean) r[4] : ((Number) r[4]).intValue() == 1);
                if (r[5] != null) dto.setUnidadAsignadaId(((Number) r[5]).longValue());
                dto.setEmail(r[6] != null ? r[6].toString() : "sin-email");
                dto.setTipoPersonal("EMPLEADO");
                dto.setNombreUnidadAsignada(nombreUnidad);
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    @Override public int getEdad(Long id) { Personal p = repo.findById(id).orElse(null); if(p==null||p.getFechaNacimiento()==null) return 0; return Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears(); }
    @Override public boolean esMayorDeEdad(Long id) { return getEdad(id) >= 18; }
    @Override public int getAntiguedadAnios(Long id) { Personal p = repo.findById(id).orElse(null); if(p==null||p.getFechaIngreso()==null) return 0; return Period.between(p.getFechaIngreso(), LocalDate.now()).getYears(); }
    @Override public long getAntiguedadMeses(Long id) { Personal p = repo.findById(id).orElse(null); if(p==null||p.getFechaIngreso()==null) return 0; return ChronoUnit.MONTHS.between(p.getFechaIngreso(), LocalDate.now()); }

    private PersonalDTO convertToDTO(Personal p) {
        try {
            if(p == null) return null;
            PersonalDTO dto = new PersonalDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre() != null ? p.getNombre() : "");
            dto.setApellido(p.getApellido() != null ? p.getApellido() : "");
            dto.setDni(p.getDni());
            dto.setFechaNacimiento(p.getFechaNacimiento());
            dto.setFechaIngreso(p.getFechaIngreso());
            dto.setActivo(p.isActivo());
            if (p.getUnidadAsignada() != null) {
                dto.setUnidadAsignadaId(p.getUnidadAsignada().getId());
                dto.setNombreUnidadAsignada(p.getUnidadAsignada().getNombreComercial());
            }

            if (p instanceof Cajero) {
                dto.setTipoPersonal("CAJERO");
                Cajero c = (Cajero) p;
                dto.setTurno(c.getTurno() != null ? c.getTurno().name() : "MANIANA");
            } else if (p instanceof Cocinero) {
                dto.setTipoPersonal("COCINERO");
                Cocinero coc = (Cocinero) p;
                dto.setCategoria(coc.getCategoria() != null ? coc.getCategoria().toString() : "LAVAPLATOS");
                dto.setEspecialidad(coc.getEspecialidad() != null ? coc.getEspecialidad().toString() : "GENERAL");
            } else {
                dto.setTipoPersonal("EMPLEADO");
            }
            dto.setEmail(p.getCredenciales() != null && p.getCredenciales().getCorreo() != null ? p.getCredenciales().getCorreo() : "sin-email");
            return dto;
        } catch (Exception e) {
            System.err.println("Error DTO personal id " + (p != null ? p.getId() : "null") + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public Personal altaConUsuario(String tipo, PersonalDTO dto, Long unidadId) {
        if (dto.getFechaNacimiento() == null) throw new IllegalArgumentException("Fecha de nacimiento obligatoria");
        int edad = Period.between(dto.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) throw new IllegalArgumentException("No se puede dar de alta menor de 18. Edad: " + edad);
        if (dto.getFechaIngreso() != null && dto.getFechaIngreso().isAfter(LocalDate.now())) throw new IllegalArgumentException("Fecha ingreso no puede ser futura");
        if (usuarioRepository.findByCorreo(dto.getEmail()).isPresent()) throw new IllegalArgumentException("El email " + dto.getEmail() + " ya está registrado");

        String passPlana = UUID.randomUUID().toString().substring(0, 8);
        UserRole userRole = userRoleRepository.findByRol(RoleType.EMPLEADO).orElseThrow(() -> new RuntimeException("No existe rol EMPLEADO"));
        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(passPlana));
        usuario.setRol(userRole);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        Personal personal;
        String tipoUpper = tipo.toUpperCase();

        switch(tipoUpper) {
            case "CAJERO" -> {
                Cajero caj = new Cajero();
                String t = dto.getTurno() != null ? dto.getTurno().toUpperCase() : "MANIANA";
                caj.setTurno(t.contains("MAN") || t.contains("MAÑ") ? Cajero.Turno.MANIANA : Cajero.Turno.NOCHE);
                caj.setTipoPersonal(PersonalType.CAJERO);
                personal = caj;
            }
            case "COCINERO" -> {
                Cocinero coc = new Cocinero();
                coc.setCategoria(dto.getCategoria() != null ? dto.getCategoria() : "GENERAL");
                coc.setEspecialidad(dto.getEspecialidad() != null ? dto.getEspecialidad() : "GENERAL");
                coc.setTipoPersonal(PersonalType.COCINERO);
                personal = coc;
            }
            case "LAVAPLATOS" -> {
                Lavaplatos lav = new Lavaplatos();
                lav.setTipoPersonal(PersonalType.LAVAPLATOS);
                personal = lav;
            }
            case "AYUDANTE" -> {
                Ayudante ayu = new Ayudante();
                ayu.setTipoPersonal(PersonalType.AYUDANTE);
                personal = ayu;
            }
            default -> throw new IllegalArgumentException("Tipo inválido: " + tipo);
        }

        personal.setNombre(dto.getNombre());
        personal.setApellido(dto.getApellido());
        personal.setDni(dto.getDni());
        personal.setFechaNacimiento(dto.getFechaNacimiento());
        personal.setFechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now());
        personal.setActivo(true);
        personal.setCredenciales(usuario);
        personal.setUnidadAsignada(unidadVentaRepository.findById(unidadId).orElseThrow());
        repo.save(personal);

        try {
            emailService.enviarMail(dto.getEmail(), "Epicentro Gourmet - Acceso", "Hola " + dto.getNombre() + "!\nUsuario: " + dto.getEmail() + "\nPass: " + passPlana);
        } catch (Exception e) { System.err.println("MAIL ERROR: " + e.getMessage()); }
        return personal;
    }

    @Override
    public Personal reactivar(Long id) {
        Personal p = repo.findByIdIncludingInactive(id).orElseThrow();
        p.setActivo(true);
        if(p.getCredenciales() != null) p.getCredenciales().setActivo(true);
        return repo.save(p);
    }

    @Override
    public Personal findByCorreo(String correo) {
        return repo.findByCredencialesCorreo(correo).orElse(null);
    }
}