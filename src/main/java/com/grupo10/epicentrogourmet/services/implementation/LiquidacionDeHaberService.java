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

import com.grupo10.epicentrogourmet.entities.*;
import com.grupo10.epicentrogourmet.repositories.*;

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

    @Override
    public double calcularTotalACobrar(Long idPersonal, Long idUnidadVenta) {
        Personal p = personalRepo.findById(idPersonal).orElseThrow();
        Costos costos = costosRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Cargá tabla costos"));

        // DIAS del festival
        long dias = 3;
        try {
            UnidadVenta uv = unidadVentaRepo.findById(idUnidadVenta).orElse(null);
            if (uv != null && uv.getFestivales() != null && !uv.getFestivales().isEmpty()) {
                Festival fest = uv.getFestivales().iterator().next();
                if (fest.getFechaInicio() != null && fest.getFechaFin() != null) {
                    dias = ChronoUnit.DAYS.between(fest.getFechaInicio(), fest.getFechaFin());
                    if (dias <= 0) dias = 3;
                }
            }
        } catch(Exception e){ dias = 3; }

        int tramos = (int)(dias / 15) + 1;
        BigDecimal sueldoBase = costos.getSueldoBase() != null ? costos.getSueldoBase() : BigDecimal.valueOf(100000);

        if (p instanceof Cocinero c) {
            String cat = c.getCategoria() != null ? c.getCategoria().toUpperCase() : "LAVAPLATOS";
            BigDecimal plus;
            if (cat.equals("COCINERO")) plus = costos.getPlusCocinero() != null ? costos.getPlusCocinero() : BigDecimal.valueOf(150000);
            else if (cat.contains("AYUDANTE")) plus = costos.getPlusAyudante() != null ? costos.getPlusAyudante() : BigDecimal.valueOf(100000);
            else plus = costos.getPlusLavaplatos() != null ? costos.getPlusLavaplatos() : BigDecimal.valueOf(50000);

            // (1) totalACobrarCocina=(int(dias/15)+1) * 50% *(sueldoBase+plusCategoria) - SIN antigüedad
            return (sueldoBase.doubleValue() + plus.doubleValue()) * 0.5 * tramos;

        } else { // Cajero
            int anios = 0;
            try { anios = personalService.getAntiguedadAnios(idPersonal); } catch(Exception e){ anios = 0; }
            BigDecimal antiguedad = BigDecimal.valueOf(anios * 5000L);

            // (2) totalACobrarCajero=(int(dias/15)+1) * 50% *(sueldoBase+antigüedad) - SIN plus
            return (sueldoBase.doubleValue() + antiguedad.doubleValue()) * 0.5 * tramos;
        }
    }

    @Override
    public LiquidacionDeHaber liquidar(Long idPersonal, Long idUnidadVenta) {
        Personal p = personalRepo.findById(idPersonal).orElseThrow();
        UnidadVenta uv = unidadVentaRepo.findById(idUnidadVenta).orElseThrow();


        long dias = 30;
        if (uv.getFestivales() != null && !uv.getFestivales().isEmpty()) {
            Festival fest = uv.getFestivales().iterator().next();
            if (fest.getFechaInicio() != null && fest.getFechaFin() != null) {
                long diff = ChronoUnit.DAYS.between(fest.getFechaInicio(), fest.getFechaFin());
                if(diff > 0 && diff <= 30) dias = diff;
                else if(diff > 30) dias = 30;
            }
        }

        double total = calcularTotalACobrar(idPersonal, idUnidadVenta);

        LiquidacionDeHaber liq = new LiquidacionDeHaber();
        liq.setPersonal(p);
        liq.setUnidadVenta(uv);
        liq.setDiasDeTrabajo((int)dias);
        liq.setTotalACobrar(total);
        liq.setFechaLiquidacion(LocalDate.now());

        return liquidacionRepo.save(liq);
    }

    @Override
    public byte[] generarReciboSueldoPdf(Long liquidacionId) {
        LiquidacionDeHaber liq = liquidacionRepo.findById(liquidacionId)
                .orElseThrow(() -> new RuntimeException("Liquidación no encontrada ID: " + liquidacionId));

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

            tableInfo.addCell(new Paragraph("Empleado: " + liq.getPersonal().getNombre() + " " + liq.getPersonal().getApellido(), textoNormal));
            tableInfo.addCell(new Paragraph("DNI: " + liq.getPersonal().getDni(), textoNormal));
            tableInfo.addCell(new Paragraph("Fecha de Liquidación: " + liq.getFechaLiquidacion(), textoNormal));
            tableInfo.addCell(new Paragraph("Unidad de Venta: " + (liq.getUnidadVenta() != null ? liq.getUnidadVenta().getNombreComercial() : "N/A"), textoNormal));

            tableInfo.setSpacingAfter(20);
            document.add(tableInfo);

            PdfPTable tableDetalle = new PdfPTable(2);
            tableDetalle.setWidthPercentage(100);

            tableDetalle.addCell(new Paragraph("Concepto", textoBold));
            tableDetalle.addCell(new Paragraph("Monto", textoBold));

            tableDetalle.addCell(new Paragraph("Total Neto a Cobrar", textoNormal));
            tableDetalle.addCell(new Paragraph("$ " + String.format("%.2f", liq.getTotalACobrar()), textoNormal));

            document.add(tableDetalle);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del recibo de sueldo", e);
        }

        return out.toByteArray();
    }
}