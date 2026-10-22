package com.grupo10.epicentrogourmet.services.implementation;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.*;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.grupo10.epicentrogourmet.dtos.RequestUnidadVentaDTO;
import com.grupo10.epicentrogourmet.repositories.IUserRoleRepository;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;

@Service("unidadVentaService")
public class UnidadVentaService implements IUnidadVentaService {

    private final IUnidadVentaRepository unidadVentaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository,
                              IUsuarioRepository usuarioRepository,
                              IUserRoleRepository userRoleRepository,
                              PasswordEncoder passwordEncoder) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override public List<UnidadVenta> getAll()
    { return unidadVentaRepository.findAll(); }
    @Override public Optional<UnidadVenta> findById(Long id)
    { return unidadVentaRepository.findById(id); }
    @Override public boolean remove(Long id)
    { if(unidadVentaRepository.existsById(id)){ unidadVentaRepository.deleteById(id); return true;} return false; }

    @Override
    public UnidadVenta insertOrUpdate(UnidadVenta uv) {
        // Si no tiene codigo o es invalido, entonces se genera
        if (uv.getCodigoUnico() == null || uv.getCodigoUnico().isEmpty() || !isCodigoValido(uv.getCodigoUnico())) {
            uv.setCodigoUnico(generarCodigoUnico(uv));
        }
        return unidadVentaRepository.save(uv);
    }

    // =========================================================
    // codigo unico
    // =========================================================
    @Override
    public String generarCodigoUnico(UnidadVenta uv) {
        String nombre = uv.getNombreComercial();
        String baseNombre = (nombre != null && nombre.length() >= 3)
                ? nombre.substring(0, 3).toUpperCase()
                : "EGT";

        // 6 dígitos usando nanoTime para unicidad
        String numeros = String.valueOf(System.nanoTime());
        numeros = numeros.substring(numeros.length() - 6);

        String sinVerificador = baseNombre + numeros; // 9 caracteres
        int suma = 0;
        for (char c : sinVerificador.toCharArray()) {
            suma += c;
        }
        int verificador = suma % 10; // dígito verificador

        return sinVerificador + verificador; // 3 + 6 + 1 = 10
    }

    @Override
    public boolean isCodigoValido(String codigo) {
        if (codigo == null || codigo.length() != 10) return false;
        String base = codigo.substring(0, 9);
        int suma = 0;
        for (char c : base.toCharArray()) {
            suma += c;
        }
        int esperado = suma % 10;
        int real = Character.getNumericValue(codigo.charAt(9));
        return esperado == real;
    }

    // =========================================================
    // Logica 2 : calculo de canon
    // =========================================================
    @Override
    public double calcularCanon(Long idUnidadVenta) {
        UnidadVenta uv = unidadVentaRepository.findById(idUnidadVenta)
                .orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));

        double costoSuperficie = uv.getSuperficieM2() * 500;

        if (uv instanceof FoodTruck) {
            FoodTruck ft = (FoodTruck) uv;
            // Food Truck: (areaSup * $500) + $2000 si requiere conexión electrica
            double plusElectricidad = ft.isRequiereConexionElectrica() ? 2000 : 0;
            return costoSuperficie + plusElectricidad;
        } else if (uv instanceof PuestoDesarmable) {
            PuestoDesarmable pd = (PuestoDesarmable) uv;
            // Puesto: (areaSup * $500) - (Tiempo de Montaje * $10)
            return costoSuperficie - (pd.getTiempoMontajeMin() * 10);
        }

        return costoSuperficie;
    }

    @Override
    @Transactional
    public void registrarUnidadConResponsable(RequestUnidadVentaDTO dto) {
        UserRole rolResponsable = userRoleRepository.findByRol(RoleType.RESPONSABLE)
                .orElseThrow(() -> new RuntimeException("El rol RESPONSABLE no existe en la base de datos"));

        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setActivo(true);
        usuario.setRol(rolResponsable);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        if ("FOODTRUCK".equalsIgnoreCase(dto.getTipoUnidad())) {
            FoodTruck foodTruck = new FoodTruck();
            foodTruck.setNombreComercial(dto.getNombreComercial());
            foodTruck.setSuperficieM2(dto.getSuperficieM2());
            foodTruck.setCodigoUnico(dto.getCodigoUnico());
            foodTruck.setPatente(dto.getPatente());
            foodTruck.setRequiereConexionElectrica(dto.isRequiereConexionElectrica());
            foodTruck.setResponsable(usuarioGuardado);

            insertOrUpdate(foodTruck);

        } else if ("PUESTODESARMABLE".equalsIgnoreCase(dto.getTipoUnidad())) {
            PuestoDesarmable puesto = new PuestoDesarmable();
            puesto.setNombreComercial(dto.getNombreComercial());
            puesto.setSuperficieM2(dto.getSuperficieM2());
            puesto.setCodigoUnico(dto.getCodigoUnico());
            puesto.setCantidadCarpas(dto.getCantidadCarpas());
            puesto.setTiempoMontajeMin(dto.getTiempoMontajeMin());
            puesto.setResponsable(usuarioGuardado);

            insertOrUpdate(puesto);

        } else {
            throw new IllegalArgumentException("Tipo de unidad no válido: " + dto.getTipoUnidad());
        }
    }
}