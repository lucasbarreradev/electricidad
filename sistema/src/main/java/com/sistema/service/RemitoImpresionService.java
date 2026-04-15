package com.sistema.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.sistema.model.Remito;
import com.sistema.model.RemitoItem;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class RemitoImpresionService {

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public void generarRemitoPdf(Remito r, OutputStream out) {

        Document document = new Document(PageSize.A4, 36, 36, 15, 80);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            agregarHeader(document, r);
            agregarDatosCliente(document, r);
            agregarCajaInfo(document, r);
            agregarTablaItems(document, r);
            agregarTotales(document, r);
            agregarFirmas(document);

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de remito", e);
        } finally {
            document.close();
        }
    }

    // ==========================================
    // HEADER (igual presupuesto)
    // ==========================================
    private void agregarHeader(Document document, Remito r) throws Exception {

        Font titulo = FontFactory.getFont(FontFactory.HELVETICA, 22, Font.BOLD);
        Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, Color.DARK_GRAY);
        Font empresaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.GRAY);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{60, 40});

        // 🔹 IZQUIERDA → TITULO + TIPO
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph p = new Paragraph();
        p.add(new Chunk("REMITO\n", titulo));

        String tipoTexto = r.getTipo() == Remito.Tipo.DEVOLUCION
                ? "DEVOLUCIÓN"
                : "ENTREGA";

        p.add(new Chunk(tipoTexto, subtitulo));

        leftCell.addElement(p);
        table.addCell(leftCell);

        try {
            Image logo = Image.getInstance(getClass().getResource("/static/img/LOGO.jpg"));
            logo.scaleToFit(400, 200);

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell(logoCell);

        } catch (Exception e) {
            PdfPCell empty = new PdfPCell();
            empty.setBorder(Rectangle.NO_BORDER);
            table.addCell(empty);
        }

        document.add(table);

        // 🔹 DATOS EMPRESA
        Paragraph empresa = new Paragraph(
                "MOBEZA ELECTRICIDAD, Acceso Norte, S/N, 2681 Etruria, Argentina",
                empresaFont
        );
        empresa.setSpacingAfter(10);
        document.add(empresa);
    }

    // ==========================================
    // DATOS CLIENTE
    // ==========================================
    private void agregarDatosCliente(Document document, Remito r) throws Exception {

        Font bold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{50, 50});

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        Paragraph para = new Paragraph();
        para.add(new Chunk("PARA\n", bold));

        if (r.getCliente() != null) {

            para.add(new Chunk(
                    "Nombre completo: " + r.getCliente().getNombre() + " " + r.getCliente().getApellido() + "\n",
                    normal
            ));

            if (r.getCliente().getDireccion() != null && !r.getCliente().getDireccion().isEmpty()) {
                para.add(new Chunk(
                        "Dirección: " + r.getCliente().getDireccion() + "\n",
                        normal
                ));
            }
            if (r.getCliente().getDni() != null && !r.getCliente().getDni().isEmpty()) {
                para.add(new Chunk("DNI: " + r.getCliente().getDni() + "\n", normal));
            }

        } else {
            para.add(new Chunk("Consumidor Final\n", normal));
        }

        leftCell.addElement(para);
        table.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph info = new Paragraph();
        info.setAlignment(Element.ALIGN_RIGHT);
        info.add(new Chunk("Remito n°:\n", normal));
        info.add(new Chunk(r.getCodigo() + "\n", bold));
        info.add(new Chunk("Fecha:\n", normal));
        info.add(new Chunk(r.getFechaEmision().format(DATE_FMT), bold));

        rightCell.addElement(info);
        table.addCell(rightCell);

        document.add(table);
        document.add(new Paragraph(" "));
    }

    // ==========================================
    // CAJA AMARILLA
    // ==========================================
    private void agregarCajaInfo(Document document, Remito r) throws Exception {

        Font white = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, Color.WHITE);
        Font whiteSmall = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.WHITE);

        Color amarillo = new Color(218, 198, 125);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{33, 33, 34});
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        table.addCell(celdaCaja("Remito n°", r.getCodigo(), white, whiteSmall, amarillo));
        table.addCell(celdaCaja("Fecha", r.getFechaEmision().format(DATE_FMT), white, whiteSmall, amarillo));
        table.addCell(celdaCaja("Tipo", r.getTipo().toString(), white, whiteSmall, amarillo));

        document.add(table);
    }

    // ==========================================
    // TABLA ITEMS
    // ==========================================
    private void agregarTablaItems(Document document, Remito r) throws Exception {

        Font header = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 9);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{50, 15, 20, 15});

        table.addCell(celdaHeader("Descripción", header));
        table.addCell(celdaHeader("Cant.", header));
        table.addCell(celdaHeader("Precio", header));
        table.addCell(celdaHeader("Subtotal", header));

        for (RemitoItem item : r.getItems()) {

            table.addCell(celdaNormal(item.getProducto().getDescripcion(), normal));
            table.addCell(celdaNormal(item.getCantidad().toString(), normal));
            table.addCell(celdaNormal("$ " + DF.format(item.getPrecioUnitario()), normal));
            table.addCell(celdaNormal("$ " + DF.format(item.getSubtotal()), normal));
        }

        document.add(table);
    }

    // ==========================================
    // TOTAL
    // ==========================================
    private void agregarTotales(Document document, Remito r) throws Exception {

        Font bold = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(15);

        PdfPCell label = new PdfPCell(new Phrase("Total (ARS):", bold));
        label.setBorder(Rectangle.NO_BORDER);
        label.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(label);

        PdfPCell value = new PdfPCell(new Phrase("$ " + DF.format(r.getTotal()), bold));
        value.setBorder(Rectangle.NO_BORDER);
        value.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(value);

        document.add(table);
    }

    // ==========================================
    // FIRMAS
    // ==========================================
    private void agregarFirmas(Document document) throws Exception {

        Font font = new Font(Font.HELVETICA, 9);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell(celdaFirma("Firma quien entrega", font));
        table.addCell(celdaFirma("Firma quien recibe", font));

        document.add(table);
    }

    // ==========================================
    // HELPERS
    // ==========================================
    private PdfPCell celdaHeader(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell celdaNormal(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell celdaCaja(String label, String value, Font bold, Font normal, Color bg) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", normal));
        p.add(new Chunk(value, bold));

        cell.addElement(p);
        return cell;
    }

    private PdfPCell celdaFirma(String texto, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(30);

        Paragraph line = new Paragraph("_________________________", font);
        line.setAlignment(Element.ALIGN_CENTER);

        Paragraph label = new Paragraph(texto, font);
        label.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(line);
        cell.addElement(label);

        return cell;
    }
}