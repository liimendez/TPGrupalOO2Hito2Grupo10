package com.grupo10.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.repositories.*;
import com.grupo10.epicentrogourmet.services.ILiquidacionDeHaberService;
import com.grupo10.epicentrogourmet.services.IPersonalService;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service("liquidacionDeHaberService")
public class LiquidacionDeHaberService implements ILiquidacionDeHaberService {

    @Autowired private ILiquidacionDeHaberRepository liquidacionRepo;
    @Autowired private IPersonalRepository personalRepo;
    @Autowired private IUnidadVentaRepository unidadVentaRepo;
    @Autowired private ICostosRepository costosRepo;
    @Autowired private IPersonalService personalService;

    @Override public List<LiquidacionDeHaber> getAll() { return liquidacionRepo.findAll(); }
    @Override public Optional<LiquidacionDeHaber> findById(Long id) { return liquidacionRepo.findById(id); }
    @Override public List<LiquidacionDeHaber> findByPersonalId(Long pid) { return liquidacionRepo.findByPersonalId(pid); }
    @Override public LiquidacionDeHaber insertOrUpdate(LiquidacionDeHaber l) { return liquidacionRepo.save(l); }
    @Override public boolean remove(Long id) { if(liquidacionRepo.existsById(id)){ liquidacionRepo.deleteById(id); return true;} return false; }

    private Costos getCostos() {
        return costosRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Cargá tabla costos"));
    }

    private Festival getFestivalActivoOUltimo(UnidadVenta uv) {
        if (uv == null || uv.getFestivales() == null || uv.getFestivales().isEmpty()) return null;
        LocalDate hoy = LocalDate.now();
        for (Festival f : uv.getFestivales()) {
            if (f.getFechaInicio() != null && f.getFechaFin() != null &&
                    !hoy.isBefore(f.getFechaInicio()) && !hoy.isAfter(f.getFechaFin())) {
                return f;
            }
        }
        return uv.getFestivales().stream()
                .filter(f -> f.getFechaFin() != null)
                .max((a,b) -> a.getFechaFin().compareTo(b.getFechaFin()))
                .orElse(uv.getFestivales().iterator().next());
    }

    // FIX: elige el festival ACTIVO o el de fecha fin más reciente, no uno al azar
    private long calcularDiasReales(Personal p, UnidadVenta uv) {
        long dias = 30;
        try {
            Festival fest = getFestivalActivoOUltimo(uv);
            if (fest != null && fest.getFechaInicio() != null && fest.getFechaFin() != null) {
                LocalDate inicioReal = fest.getFechaInicio();
                if (p != null && p.getFechaIngreso() != null && p.getFechaIngreso().isAfter(inicioReal)) {
                    inicioReal = p.getFechaIngreso();
                }
                long diff = ChronoUnit.DAYS.between(inicioReal, fest.getFechaFin()) + 1;
                if (diff > 0) dias = diff;
                else dias = 1;
            }
        } catch (Exception e) {}
        return dias;
    }

    @Override
    public double calcularTotalACobrar(Long idPersonal, Long idUnidadVenta) {
        Personal p = personalRepo.findById(idPersonal).orElse(null);
        UnidadVenta uv = idUnidadVenta != null ? unidadVentaRepo.findById(idUnidadVenta).orElse(null) : null;
        long dias = calcularDiasReales(p, uv);
        return calcularTotalACobrarConDias(idPersonal, dias);
    }

    private double calcularTotalACobrarConDias(Long idPersonal, long dias) {
        Personal p = personalRepo.findById(idPersonal).orElseThrow();
        Costos costos = getCostos();

        // FIX: todo desde Costos, nada hardcodeado
        int diasPorTramo = costos.getDiasPorTramo() != null ? costos.getDiasPorTramo() : 15;
        BigDecimal factorFestival = costos.getFactorFestival() != null ? costos.getFactorFestival() : new BigDecimal("0.5");
        BigDecimal valorAntiguedad = costos.getValorAntiguedadPorAnio() != null ? costos.getValorAntiguedadPorAnio() : new BigDecimal("5000");
        BigDecimal sueldoBase = costos.getSueldoBase() != null ? costos.getSueldoBase() : new BigDecimal("100000");

        int tramos = (int)(dias / diasPorTramo) + 1;

        int anios = 0;
        try { anios = personalService.getAntiguedadAnios(idPersonal); } catch(Exception e){ anios = 0; }
        BigDecimal antiguedad = valorAntiguedad.multiply(BigDecimal.valueOf(anios));

        BigDecimal total;
        if (p instanceof Cocinero c) {
            BigDecimal plus = c.getCategoriaCocina() != null ? c.getCategoriaCocina().getPlusFijo() : new BigDecimal("150000");
            total = new BigDecimal(tramos).multiply(factorFestival).multiply(sueldoBase.add(plus));
        } else {
            total = new BigDecimal(tramos).multiply(factorFestival).multiply(sueldoBase.add(antiguedad));
        }
        return total.doubleValue();
    }

    @Override
    public LiquidacionDeHaber liquidar(Long idPersonal, Long idUnidadVenta) {
        Personal p = personalRepo.findById(idPersonal).orElseThrow();
        UnidadVenta uv = unidadVentaRepo.findById(idUnidadVenta).orElseThrow();

        // === VALIDACIÓN: 1 LIQUIDACIÓN POR EMPLEADO POR FESTIVAL ===
        Festival fest = getFestivalActivoOUltimo(uv);
        if (fest != null && fest.getFechaInicio() != null && fest.getFechaFin() != null) {
            boolean yaLiquidado = liquidacionRepo.existsByPersonal_IdAndFechaLiquidacionBetween(
                    idPersonal, fest.getFechaInicio(), fest.getFechaFin());
            if (yaLiquidado) {
                throw new RuntimeException("El empleado " + p.getNombre() + " " + p.getApellido()
                        + " ya fue liquidado en " + fest.getNombre() + ". No se puede liquidar 2 veces el mismo festival.");
            }
        } else {
            // Fallback: si no hay festival, no liquidar 2 veces el mismo día
            if (liquidacionRepo.existsByPersonal_IdAndFechaLiquidacion(idPersonal, LocalDate.now())) {
                throw new RuntimeException("El empleado ID " + idPersonal + " ya fue liquidado hoy " + LocalDate.now());
            }
        }

        long dias = calcularDiasReales(p, uv);
        double total = calcularTotalACobrarConDias(idPersonal, dias);

        LiquidacionDeHaber liq = new LiquidacionDeHaber();
        liq.setPersonal(p);
        liq.setUnidadVenta(uv);
        liq.setDiasDeTrabajo((int)dias);
        liq.setTotalACobrar(total);
        liq.setFechaLiquidacion(LocalDate.now());

        return liquidacionRepo.save(liq);
    }

    @Override
    public boolean existsByPersonal_IdAndFechaLiquidacionBetween(
            Long personalId, LocalDate desde, LocalDate hasta) {
        return liquidacionRepo
                .existsByPersonal_IdAndFechaLiquidacionBetween(personalId, desde, hasta);
    }

    @Override
    public byte[] generarReciboSueldoPdf(Long liquidacionId) {
        LiquidacionDeHaber liq = liquidacionRepo.findById(liquidacionId)
                .orElseThrow(() -> new RuntimeException("Liquidación no encontrada ID: " + liquidacionId));

        Personal p = liq.getPersonal();
        UnidadVenta uv = liq.getUnidadVenta();
        Costos costos = costosRepo.findAll().stream().findFirst().orElse(null);
        int anios = 0;
        try { anios = personalService.getAntiguedadAnios(p.getId()); } catch(Exception e){ anios = 0; }
        BigDecimal sueldoBase = costos != null && costos.getSueldoBase() != null ? costos.getSueldoBase() : new BigDecimal("100000");
        BigDecimal valorAntiguedad = costos != null && costos.getValorAntiguedadPorAnio() != null ? costos.getValorAntiguedadPorAnio() : new BigDecimal("5000");
        BigDecimal factorFestival = costos != null && costos.getFactorFestival() != null ? costos.getFactorFestival() : new BigDecimal("0.5");
        int diasPorTramo = costos != null && costos.getDiasPorTramo() != null ? costos.getDiasPorTramo() : 15;
        BigDecimal plus = BigDecimal.ZERO;
        String categoriaNombre = "CAJERO";
        if (p instanceof Cocinero c && c.getCategoriaCocina() != null) {
            plus = c.getCategoriaCocina().getPlusFijo() != null ? c.getCategoriaCocina().getPlusFijo() : new BigDecimal("150000");
            categoriaNombre = c.getCategoriaCocina().getNombre();
        }
        int tramos = (liq.getDiasDeTrabajo() / diasPorTramo) + 1;
        BigDecimal antiguedad = valorAntiguedad.multiply(BigDecimal.valueOf(anios));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font subTituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY);
            Font textoBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font textoNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            Paragraph titulo = new Paragraph("EPICENTRO GOURMET S.A.", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            Paragraph sub = new Paragraph("RECIBO DE SUELDO - Liquidación N°: " + liq.getId(), subTituloFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            PdfPTable tableInfo = new PdfPTable(2);
            tableInfo.setWidthPercentage(100);
            tableInfo.addCell(new Paragraph("Empleado: " + p.getNombre() + " " + p.getApellido(), textoNormal));
            tableInfo.addCell(new Paragraph("DNI: " + p.getDni(), textoNormal));
            tableInfo.addCell(new Paragraph("Fecha Liquidación: " + liq.getFechaLiquidacion(), textoNormal));
            tableInfo.addCell(new Paragraph("Unidad: " + (uv != null ? uv.getNombreComercial() : "N/A") + " - " + (uv != null ? uv.getCodigoUnico() : ""), textoNormal));
            tableInfo.addCell(new Paragraph("Días Trabajados: " + liq.getDiasDeTrabajo() + " (desde " + (p.getFechaIngreso() != null ? p.getFechaIngreso() : "inicio") + ")", textoNormal));
            tableInfo.addCell(new Paragraph("Antigüedad: " + anios + " años | Categoría: " + categoriaNombre, textoNormal));
            tableInfo.setSpacingAfter(20);
            document.add(tableInfo);

            PdfPTable tableDetalle = new PdfPTable(4);
            tableDetalle.setWidthPercentage(100);
            tableDetalle.addCell(new Paragraph("COD", textoBold));
            tableDetalle.addCell(new Paragraph("CONCEPTO", textoBold));
            tableDetalle.addCell(new Paragraph("CANT.", textoBold));
            tableDetalle.addCell(new Paragraph("MONTO", textoBold));

            tableDetalle.addCell(new Paragraph("1000", textoNormal));
            tableDetalle.addCell(new Paragraph("Sueldo Básico Convenio", textoNormal));
            tableDetalle.addCell(new Paragraph(String.valueOf(liq.getDiasDeTrabajo()), textoNormal));
            tableDetalle.addCell(new Paragraph("$ " + sueldoBase, textoNormal));

            tableDetalle.addCell(new Paragraph("1001", textoNormal));
            tableDetalle.addCell(new Paragraph("Antigüedad (" + anios + " años x $" + valorAntiguedad + ")", textoNormal));
            tableDetalle.addCell(new Paragraph(String.valueOf(anios), textoNormal));
            tableDetalle.addCell(new Paragraph("$ " + antiguedad, textoNormal));

            tableDetalle.addCell(new Paragraph("1002", textoNormal));
            tableDetalle.addCell(new Paragraph("Plus por Categoría - " + categoriaNombre, textoNormal));
            tableDetalle.addCell(new Paragraph("1", textoNormal));
            tableDetalle.addCell(new Paragraph("$ " + plus, textoNormal));

            tableDetalle.addCell(new Paragraph("1003", textoNormal));
            tableDetalle.addCell(new Paragraph("Factor Festival (" + factorFestival.multiply(new BigDecimal("100")) + "% - PDF punto 4)", textoNormal));
            tableDetalle.addCell(new Paragraph(String.valueOf(factorFestival), textoNormal));
            tableDetalle.addCell(new Paragraph("-", textoNormal));

            tableDetalle.addCell(new Paragraph("1004", textoNormal));
            tableDetalle.addCell(new Paragraph("Tramos Festival = int(" + liq.getDiasDeTrabajo() + "/" + diasPorTramo + ")+1", textoNormal));
            tableDetalle.addCell(new Paragraph(String.valueOf(tramos), textoNormal));
            tableDetalle.addCell(new Paragraph("-", textoNormal));

            tableDetalle.addCell(new Paragraph("", textoBold));
            tableDetalle.addCell(new Paragraph("Cálculo: (" + sueldoBase + " + " + antiguedad + " + " + plus + ") * " + factorFestival + " * " + tramos, textoBold));
            tableDetalle.addCell(new Paragraph("", textoBold));
            tableDetalle.addCell(new Paragraph("$ " + String.format("%.2f", liq.getTotalACobrar()), textoBold));

            tableDetalle.addCell(new Paragraph("", textoBold));
            tableDetalle.addCell(new Paragraph("TOTAL BRUTO / NETO A COBRAR", textoBold));
            tableDetalle.addCell(new Paragraph("", textoBold));
            tableDetalle.addCell(new Paragraph("$ " + String.format("%.2f", liq.getTotalACobrar()), textoBold));

            document.add(tableDetalle);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del recibo de sueldo", e);
        }
        return out.toByteArray();
    }
}