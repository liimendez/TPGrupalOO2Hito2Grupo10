package com.grupo10.epicentrogourmet.services.implementation;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.entities.FoodTruck;
import com.grupo10.epicentrogourmet.entities.PuestoDesarmable;
import com.grupo10.epicentrogourmet.entities.UnidadVenta;
import com.grupo10.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;

@Service("unidadVentaService")
public class UnidadVentaService implements IUnidadVentaService {

    @Autowired
    private IUnidadVentaRepository unidadVentaRepository;

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
}