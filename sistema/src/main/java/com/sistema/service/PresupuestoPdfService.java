package com.sistema.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.sistema.model.CondicionIva;
import com.sistema.model.DetallePresupuesto;
import com.sistema.model.Presupuesto;
import com.sistema.model.PresupuestoFooter;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PresupuestoPdfService {

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public void generarPdf(Presupuesto p, OutputStream out) {
        Document document = new Document(PageSize.A4, 36, 36, 15, 80);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new PresupuestoFooter());

            document.open();

            agregarHeader(document, p);
            agregarDatosCliente(document, p);
            agregarCajaInfo(document, p);
            agregarFormaPago(document, p);
            agregarTablaItems(document, p);
            agregarTotales(document, p);

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        } finally {
            document.close();
        }
    }

    // ==========================================
    // HEADER: Título + Logo + Empresa
    // ==========================================
    private void agregarHeader(Document document, Presupuesto p) throws Exception {
        Font titulo = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD);
        Font empresaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{50, 50});

        // TÍTULO
        PdfPCell tituloCell = new PdfPCell(new Phrase("Presupuesto", titulo));
        tituloCell.setBorder(Rectangle.NO_BORDER);
        tituloCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tituloCell.setPaddingTop(0);
        table.addCell(tituloCell);

        // LOGO (derecha)
        try {
            Image logo = Image.getInstance(getClass().getResource("/static/img/LOGO.jpg"));
            logo.scaleToFit(400, 200);

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setPaddingTop(0); //
            table.addCell(logoCell);
        } catch (Exception e) {
            // Si no encuentra logo, celda vacía
            PdfPCell empty = new PdfPCell();
            empty.setBorder(Rectangle.NO_BORDER);
            table.addCell(empty);
        }

        document.add(table);

        // DATOS EMPRESA (gris, debajo del título)
        Paragraph empresa = new Paragraph(
                "MOBEZA ELECTRICIDAD, Acceso Norte, S/N, 2681 Etruria, Argentina",
                empresaFont
        );
        empresa.setSpacingAfter(10);
        document.add(empresa);
    }

    // ==========================================
    // DATOS CLIENTE (PARA)
    // ==========================================
    private void agregarDatosCliente(Document document, Presupuesto p) throws Exception {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{50, 50});

        // COLUMNA IZQUIERDA: Cliente
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        Paragraph para = new Paragraph();
        para.add(new Chunk("PARA\n", bold));

        String clienteInfo = p.getCliente() != null
                ? p.getCliente().getNombre() + " " + p.getCliente().getApellido() + "\n" +
                (p.getCliente().getDireccion() != null ? p.getCliente().getDireccion() + "\n" : "")
                : "Consumidor Final\n";

        para.add(new Chunk(clienteInfo, normal));
        leftCell.addElement(para);
        table.addCell(leftCell);

        // COLUMNA DERECHA: Info presupuesto
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph info = new Paragraph();
        info.setAlignment(Element.ALIGN_RIGHT);
        info.add(new Chunk("Presupuesto n°:\n", normal));
        info.add(new Chunk(p.getCodigo() + "\n", bold));
        info.add(new Chunk("Fecha de emisión:\n", normal));
        info.add(new Chunk(p.getFecha().format(DATE_FMT) + "\n", bold));
        info.add(new Chunk("Válido hasta:\n", normal));
        info.add(new Chunk(p.getFecha().plusDays(30).format(DATE_FMT), bold));

        rightCell.addElement(info);
        table.addCell(rightCell);

        document.add(table);
        document.add(new Paragraph(" ")); // Espacio
    }

    // ==========================================
    // CAJA AMARILLA CON INFO
    // ==========================================
    private void agregarCajaInfo(Document document, Presupuesto p) throws Exception {
        Font white = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
        Font whiteSmall = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, BaseColor.WHITE);

        BaseColor amarillo = new BaseColor(218, 198, 125);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{25, 25, 25, 25});
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        // Presupuesto n°
        PdfPCell cell1 = new PdfPCell();
        cell1.setBackgroundColor(amarillo);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(8);
        Paragraph p1 = new Paragraph();
        p1.add(new Chunk("Presupuesto n°:\n", whiteSmall));
        p1.add(new Chunk(p.getCodigo(), white));
        cell1.addElement(p1);
        table.addCell(cell1);

        // Fecha emisión
        PdfPCell cell2 = new PdfPCell();
        cell2.setBackgroundColor(amarillo);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setPadding(8);
        Paragraph p2 = new Paragraph();
        p2.add(new Chunk("Fecha de emisión:\n", whiteSmall));
        p2.add(new Chunk(p.getFecha().format(DATE_FMT), white));
        cell2.addElement(p2);
        table.addCell(cell2);

        // Válido hasta
        PdfPCell cell3 = new PdfPCell();
        cell3.setBackgroundColor(amarillo);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setPadding(8);
        Paragraph p3 = new Paragraph();
        p3.add(new Chunk("Válido hasta:\n", whiteSmall));
        p3.add(new Chunk(p.getFecha().plusDays(30).format(DATE_FMT), white));
        cell3.addElement(p3);
        table.addCell(cell3);

        // Total a pagar
        PdfPCell cell4 = new PdfPCell();
        cell4.setBackgroundColor(amarillo);
        cell4.setBorder(Rectangle.NO_BORDER);
        cell4.setPadding(8);
        Paragraph p4 = new Paragraph();
        p4.add(new Chunk("Total a pagar\n", whiteSmall));

        // Total final ya incluye IVA, si es consumidor final no discriminamos
        boolean esConsumidorFinal = p.getCliente() == null ||
                p.getCliente().getCondicionIva() == CondicionIva.CONSUMIDOR_FINAL;

        BigDecimal totalFinal = p.getTotal();

        if (esConsumidorFinal) {
            // consumidor final: todo va en total, no mostramos IVA
            p4.add(new Chunk("$ " + DF.format(totalFinal), white));
        } else {
            // clientes que no son consumidor final: total con IVA incluido
            p4.add(new Chunk("$ " + DF.format(totalFinal), white));
        }

        cell4.addElement(p4);
        table.addCell(cell4);

        document.add(table);
    }

    // ==========================================
// FORMA DE PAGO
// ==========================================
    private void agregarFormaPago(Document document, Presupuesto p) throws Exception {

        Font bold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);
        table.setWidths(new int[]{25, 75});

        PdfPCell label = new PdfPCell(new Phrase("Forma de pago:", bold));
        label.setBorder(Rectangle.NO_BORDER);
        label.setPadding(4);
        table.addCell(label);

        String formaPago = p.getFormaPago() != null
                ? p.getFormaPago().toString()
                : "No especificada";

        PdfPCell value = new PdfPCell(new Phrase(formaPago, normal));
        value.setBorder(Rectangle.NO_BORDER);
        value.setPadding(4);
        table.addCell(value);

        document.add(table);
    }


    // ==========================================
    // TABLA DE ITEMS CON IVA
    // ==========================================
    private void agregarTablaItems(Document document, Presupuesto p) throws Exception {

        Font header = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 9);

        // Columnas: Descripción | Cant | Precio Unitario | IVA | Importe
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{45, 12, 18, 12, 13});

        // Headers
        table.addCell(celdaHeader("Descripción", header));
        table.addCell(celdaHeader("Cant.", header));
        table.addCell(celdaHeader("Precio Unitario ($)", header));
        table.addCell(celdaHeader("IVA ($)", header));
        table.addCell(celdaHeader("Importe ($)", header));

        boolean esConsumidorFinal = p.getCliente() == null ||
                p.getCliente().getCondicionIva() == CondicionIva.CONSUMIDOR_FINAL;

        for (DetallePresupuesto d : p.getDetalles()) {

            BigDecimal subtotalConIva = d.getSubtotal(); // 👈 YA TIENE DESCUENTO
            BigDecimal cantidad = BigDecimal.valueOf(d.getCantidad());

            BigDecimal alicuotaIva = d.getAlicuotaIva() != null
                    ? d.getAlicuotaIva()
                    : BigDecimal.ZERO;

            BigDecimal ivaRate = alicuotaIva
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

            // 🔹 Neto e IVA DESDE subtotal
            BigDecimal netoItem = subtotalConIva.divide(
                    BigDecimal.ONE.add(ivaRate),
                    2,
                    RoundingMode.HALF_UP
            );

            BigDecimal ivaItem = subtotalConIva.subtract(netoItem);

            // Precio unitario neto (ya con descuento prorrateado)
            BigDecimal precioUnitarioNeto = netoItem.divide(
                    cantidad,
                    2,
                    RoundingMode.HALF_UP
            );

            // IVA total por línea (no unitario)
            BigDecimal ivaLinea = ivaItem;

            // Consumidor final: no discriminamos IVA
            if (esConsumidorFinal) {
                precioUnitarioNeto = subtotalConIva.divide(
                        cantidad,
                        2,
                        RoundingMode.HALF_UP
                );
                ivaLinea = BigDecimal.ZERO;
            }

            // ---------------- CELDAS ----------------

            // Descripción
            PdfPCell descCell = new PdfPCell(
                    new Phrase(d.getProducto().getDescripcion(), normal)
            );
            descCell.setPadding(6);
            descCell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(descCell);

            // Cantidad
            table.addCell(celdaNormal(cantidad.toString(), normal));

            // Precio unitario (neto o final según condición IVA)
            table.addCell(celdaNormal(DF.format(precioUnitarioNeto), normal));

            // IVA por línea
            table.addCell(celdaNormal(DF.format(ivaLinea), normal));

            // Importe total (subtotal con IVA y descuento)
            table.addCell(celdaNormal(DF.format(subtotalConIva), normal));
        }

        document.add(table);
    }




    // ==========================================
    // TOTALES CON IVA
    // ==========================================
    private void agregarTotales(Document document, Presupuesto p) throws Exception {
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font bold = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);

        boolean esConsumidorFinal = p.getCliente() == null ||
                p.getCliente().getCondicionIva() == CondicionIva.CONSUMIDOR_FINAL;

        BigDecimal totalNeto = BigDecimal.ZERO;
        Map<BigDecimal, BigDecimal> ivasMap = new HashMap<>(); // IVA acumulado por alícuota

        for (DetallePresupuesto d : p.getDetalles()) {

            BigDecimal subtotalConIva = d.getSubtotal(); // 👈 YA TIENE DESCUENTO
            BigDecimal alicuotaIva = d.getAlicuotaIva() != null
                    ? d.getAlicuotaIva()
                    : BigDecimal.ZERO;

            BigDecimal ivaRate = alicuotaIva
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

            BigDecimal netoItem = subtotalConIva.divide(
                    BigDecimal.ONE.add(ivaRate),
                    2,
                    RoundingMode.HALF_UP
            );

            BigDecimal ivaItem = subtotalConIva.subtract(netoItem);

            // Consumidor final: no discriminamos IVA
            if (esConsumidorFinal) {
                netoItem = subtotalConIva;
                ivaItem = BigDecimal.ZERO;
            }

            totalNeto = totalNeto.add(netoItem);

            if (alicuotaIva.compareTo(BigDecimal.ZERO) > 0) {
                ivasMap.merge(alicuotaIva, ivaItem, BigDecimal::add);
            }
        }



        BigDecimal totalIva = ivasMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFinal = totalNeto.add(totalIva);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(15);

        if (esConsumidorFinal) {
            // Consumidor final: solo mostrar total
            PdfPCell labelTotal = new PdfPCell(new Phrase("Total (ARS):", bold));
            labelTotal.setBorder(Rectangle.NO_BORDER);
            labelTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelTotal.setPadding(4);
            table.addCell(labelTotal);

            PdfPCell valorTotal = new PdfPCell(new Phrase("$ " + DF.format(totalFinal), bold));
            valorTotal.setBorder(Rectangle.NO_BORDER);
            valorTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valorTotal.setPadding(4);
            table.addCell(valorTotal);

        } else {
            // Mostrar neto
            PdfPCell labelNeto = new PdfPCell(new Phrase("Total neto", normal));
            labelNeto.setBorder(Rectangle.NO_BORDER);
            labelNeto.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelNeto.setPadding(4);
            table.addCell(labelNeto);

            PdfPCell valorNeto = new PdfPCell(new Phrase("$ " + DF.format(totalNeto), normal));
            valorNeto.setBorder(Rectangle.NO_BORDER);
            valorNeto.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valorNeto.setPadding(4);
            table.addCell(valorNeto);

            // Mostrar IVA discriminado por alícuota
            for (Map.Entry<BigDecimal, BigDecimal> entry : ivasMap.entrySet()) {
                BigDecimal alicuota = entry.getKey();
                BigDecimal iva = entry.getValue();

                if (alicuota.compareTo(BigDecimal.ZERO) > 0 && iva.compareTo(BigDecimal.ZERO) > 0) {
                    PdfPCell labelIva = new PdfPCell(new Phrase("IVA " + DF.format(alicuota) + " %", normal));
                    labelIva.setBorder(Rectangle.NO_BORDER);
                    labelIva.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    labelIva.setPadding(4);
                    table.addCell(labelIva);

                    PdfPCell valorIva = new PdfPCell(new Phrase("$ " + DF.format(iva), normal));
                    valorIva.setBorder(Rectangle.NO_BORDER);
                    valorIva.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    valorIva.setPadding(4);
                    table.addCell(valorIva);
                }
            }

            // Total final
            PdfPCell labelTotal = new PdfPCell(new Phrase("Total (ARS):", bold));
            labelTotal.setBorder(Rectangle.NO_BORDER);
            labelTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelTotal.setPadding(4);
            table.addCell(labelTotal);

            PdfPCell valorTotal = new PdfPCell(new Phrase("$ " + DF.format(totalFinal), bold));
            valorTotal.setBorder(Rectangle.NO_BORDER);
            valorTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valorTotal.setPadding(4);
            table.addCell(valorTotal);
        }

        document.add(table);
    }



    // ==========================================
    // HELPERS
    // ==========================================
    private PdfPCell celdaHeader(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(BaseColor.GRAY);
        return cell;
    }

    private PdfPCell celdaNormal(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }
}

