package com.sistema.model;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PresupuestoFooter extends PdfPageEventHelper {

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Font bold = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
        Font normal = new Font(Font.FontFamily.HELVETICA, 8);

        PdfContentByte cb = writer.getDirectContent();

        // Posición del footer
        float y = document.bottom() - 15;

        // Línea horizontal
        cb.setLineWidth(0.5f);
        cb.moveTo(document.left(), y + 20);
        cb.lineTo(document.right(), y + 20);
        cb.stroke();

        // Tabla para layout horizontal
        PdfPTable table = new PdfPTable(3);
        table.setTotalWidth(document.right() - document.left());

        try {
            table.setWidths(new int[]{33, 33, 34});

            // NOMBRE + ICONO
            PdfPCell cell1 = new PdfPCell();
            cell1.setBorder(Rectangle.NO_BORDER);
            Paragraph p1 = new Paragraph();
            p1.add(new Chunk("👤 ", new Font(Font.FontFamily.HELVETICA, 30)));
            p1.add(new Chunk("Nery Pelaye", bold));
            cell1.addElement(p1);
            table.addCell(cell1);

            // TELÉFONO + ICONO
            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph p2 = new Paragraph();
            p2.setAlignment(Element.ALIGN_CENTER);
            p2.add(new Chunk("📞 ", new Font(Font.FontFamily.HELVETICA, 30)));
            p2.add(new Chunk("3534082798", bold));
            cell2.addElement(p2);
            table.addCell(cell2);

            // EMAIL + ICONO
            PdfPCell cell3 = new PdfPCell();
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph p3 = new Paragraph();
            p3.setAlignment(Element.ALIGN_RIGHT);
            p3.add(new Chunk("✉ ", new Font(Font.FontFamily.HELVETICA, 30)));
            p3.add(new Chunk("nerypelaye@gmail.com", bold));
            cell3.addElement(p3);
            table.addCell(cell3);

            table.writeSelectedRows(0, -1, document.left(), y + 15, cb);

            // Dirección empresa (centrado abajo)
            ColumnText ct = new ColumnText(cb);
            ct.setSimpleColumn(document.left(), y - 20, document.right(), y);

            Paragraph empresa = new Paragraph();
            empresa.setAlignment(Element.ALIGN_LEFT);
            empresa.add(new Chunk(
                    "MOBEZA ELECTRICIDAD\n" +
                            "Acceso Norte, S/N\n" +
                            "2681 Etruria\n" +
                            "Argentina",
                    normal
            ));
            ct.addElement(empresa);
            ct.go();

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}



