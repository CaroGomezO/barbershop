package com.example.barbershop.infrastructure.adapter.out.persistence.pdf;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.dto.AgendaItemResponse;
import com.example.barbershop.application.dto.BarberAgendaResponse;
import com.example.barbershop.application.usecase.AgendaPdfExporter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


@Component
public class AgendaPdfExporterImpl implements AgendaPdfExporter {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public byte[] exportar(BarberAgendaResponse agenda) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer     = new PdfWriter(baos);
            PdfDocument pdfDoc   = new PdfDocument(writer);
            Document document    = new Document(pdfDoc);

            // Título
            document.add(new Paragraph("Agenda del Barbero")
                    .setBold().setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));

            // Subtítulo con rango
            document.add(new Paragraph(
                    "Vista: " + agenda.getVista()
                    + "   |   Período: " + agenda.getFechaDesde()
                    + " → " + agenda.getFechaHasta())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Generado: " + LocalDateTime.now().format(DT_FMT))
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph(" "));

            if (agenda.getCitas().isEmpty()) {
                document.add(new Paragraph(agenda.getMensaje())
                        .setItalic().setTextAlignment(TextAlignment.CENTER));
            } else {
                // Tabla de citas
                Table table = new Table(UnitValue.createPercentArray(
                        new float[]{5, 20, 22, 12, 10, 10, 14}))
                        .useAllAvailableWidth();

                // Cabecera
                String[] headers = {"#", "Cliente", "Servicio", "Fecha", "Hora", "Fin", "Estado"};
                for (String h : headers) {
                    table.addHeaderCell(new Cell()
                            .add(new Paragraph(h).setBold())
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                }

                // Filas
                int i = 1;
                for (AgendaItemResponse cita : agenda.getCitas()) {
                    // Las citas canceladas se muestran con fondo rojo claro
                    var bgColor = cita.isCancelada()
                            ? new com.itextpdf.kernel.colors.DeviceRgb(255, 204, 204)
                            : null;

                    addCell(table, String.valueOf(i++), bgColor);
                    addCell(table, cita.getNombreCliente(), bgColor);
                    addCell(table, cita.getServicio(), bgColor);
                    addCell(table, cita.getFecha(), bgColor);
                    addCell(table, cita.getHora(), bgColor);
                    addCell(table, cita.getHoraFin(), bgColor);
                    addCell(table, cita.getEstado(), bgColor);
                }

                document.add(table);

                document.add(new Paragraph(" "));
                document.add(new Paragraph("Total de citas: " + agenda.getTotalCitas())
                        .setBold());
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la agenda", e);
        }
    }

    private void addCell(Table table, String text,
                         com.itextpdf.kernel.colors.Color bgColor) {
        Cell cell = new Cell().add(new Paragraph(text).setFontSize(9));
        if (bgColor != null) {
            cell.setBackgroundColor(bgColor);
        }
        table.addCell(cell);
    }
}
