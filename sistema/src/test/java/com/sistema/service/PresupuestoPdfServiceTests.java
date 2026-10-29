package com.sistema.service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.sistema.model.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PresupuestoPdfServiceTests {
    @Test
    void descripcionLargaComienzaEnPrimeraHojaYConservaTextoYEncabezados() throws Exception {
        String descripcion;
        try (var stream = getClass().getResourceAsStream("/presupuesto-descripcion-larga.txt")) {
            descripcion = new String(stream.readAllBytes(), StandardCharsets.UTF_8).strip();
        }
        Presupuesto presupuesto = presupuesto(descripcion, "10");
        byte[] pdf = generar(presupuesto);
        Files.createDirectories(Path.of("target/pdf-verificacion"));
        Files.write(Path.of("target/pdf-verificacion/presupuesto-largo.pdf"), pdf);
        PdfReader reader = new PdfReader(pdf);
        try {
            assertThat(reader.getNumberOfPages()).isGreaterThan(1);
            assertThat(PdfTextExtractor.getTextFromPage(reader, 1)).contains("TRABAJO A REALIZAR");
            StringBuilder contenido = new StringBuilder();
            for (int pagina = 1; pagina <= reader.getNumberOfPages(); pagina++) {
                String texto = PdfTextExtractor.getTextFromPage(reader, pagina);
                assertThat(texto).contains("Presupuesto", "MOBEZA ELECTRICIDAD ·", "Descripción", "Desc. %");
                // Cada hoja conserva el logo de la empresa.
                assertThat(reader.getPageN(pagina).getAsDict(com.itextpdf.text.pdf.PdfName.RESOURCES)
                        .getAsDict(com.itextpdf.text.pdf.PdfName.XOBJECT)).isNotNull();
                contenido.append(texto).append(' ');
            }
            String normalizado = contenido.toString().replaceAll("\\s+", " ");
            // Se comprueba cada palabra en orden, aun si un encabezado interrumpe un párrafo.
            int posicion = 0;
            for (String palabra : descripcion.split("\\s+")) {
                int encontrada = normalizado.indexOf(palabra, posicion);
                assertThat(encontrada).as("Palabra conservada: %s", palabra).isGreaterThanOrEqualTo(posicion);
                posicion = encontrada + palabra.length();
            }
        } finally {
            reader.close();
        }
    }

    @Test
    void muestraPrecioOriginalPorcentajeEImporteConDescuento() throws Exception {
        String texto = texto(generar(presupuesto("Instalación eléctrica", "10")));
        assertThat(texto).contains("Desc. %", formato("1210"), formato("10") + " %", formato("2178"),
                "Descuento total incluido (con IVA): $ " + formato("242"));
    }

    @Test
    void respetaIvaDiscriminadoYConversionUsdConDescuento() throws Exception {
        Presupuesto presupuesto = presupuesto("Instalación eléctrica", "10");
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente");
        cliente.setApellido("Prueba");
        cliente.setCondicionIva(CondicionIva.RESPONSABLE_INSCRIPTO);
        presupuesto.setCliente(cliente);
        presupuesto.setMoneda(Presupuesto.Moneda.USD);
        presupuesto.setTipoCambio(new BigDecimal("100"));
        String texto = texto(generar(presupuesto));
        assertThat(texto).contains("USD", formato("10") + " %", formato("10"), formato("3.78"), formato("21.78"),
                "Descuento total incluido (con IVA): U$D " + formato("2.42"));
    }

    @Test
    void presupuestoCortoSinDescuentoSigueEnUnaHoja() throws Exception {
        byte[] pdf = generar(presupuesto("Producto de prueba", "0"));
        PdfReader reader = new PdfReader(pdf);
        try {
            assertThat(reader.getNumberOfPages()).isEqualTo(1);
            assertThat(PdfTextExtractor.getTextFromPage(reader, 1)).contains("Producto de prueba").doesNotContain("Desc. %");
        } finally {
            reader.close();
        }
    }

    private Presupuesto presupuesto(String descripcion, String descuento) {
        Presupuesto p = new Presupuesto();
        p.setCodigo("PRUEBA");
        p.setFecha(LocalDateTime.of(2026, 10, 28, 12, 0));
        p.setFormaPago(FormaPago.CONTADO);
        DetallePresupuesto d = new DetallePresupuesto();
        d.setDescripcion(descripcion);
        d.setCantidad(2);
        d.setPrecioUnitario(new BigDecimal("1210"));
        d.setAlicuotaIva(new BigDecimal("21"));
        d.setDescuentoPct(new BigDecimal(descuento));
        d.calcularSubtotal();
        p.agregarDetalle(d);
        p.calcularTotal();
        return p;
    }

    private byte[] generar(Presupuesto p) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new PresupuestoPdfService().generarPdf(p, out);
        return out.toByteArray();
    }

    private String texto(byte[] pdf) throws Exception {
        PdfReader reader = new PdfReader(pdf);
        try {
            return PdfTextExtractor.getTextFromPage(reader, 1);
        } finally {
            reader.close();
        }
    }

    private String formato(String numero) {
        return new DecimalFormat("#,##0.00").format(new BigDecimal(numero));
    }
}
