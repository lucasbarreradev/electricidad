package com.sistema.service;

import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.sistema.model.Remito;
import com.sistema.model.RemitoItem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RemitoImpresionServiceTests {
    @Test
    void repiteEncabezadoYColumnasYConservaTodosLosItems() throws Exception {
        Remito remito = remito();
        for (int i = 1; i <= 65; i++) {
            agregar(remito, String.format("Material de prueba %03d", i));
        }
        byte[] pdf = generar(remito);
        Files.createDirectories(Path.of("target/pdf-verificacion"));
        Files.write(Path.of("target/pdf-verificacion/remito-varias-hojas.pdf"), pdf);
        PdfReader reader = new PdfReader(pdf);
        try {
            assertThat(reader.getNumberOfPages()).isGreaterThan(1);
            StringBuilder completo = new StringBuilder();
            for (int pagina = 1; pagina <= reader.getNumberOfPages(); pagina++) {
                String texto = PdfTextExtractor.getTextFromPage(reader, pagina);
                assertThat(texto).contains("MOBEZA ELECTRICIDAD", "REMITO", "R-PRUEBA");
                assertThat(reader.getPageN(pagina).getAsDict(PdfName.RESOURCES)
                        .getAsDict(PdfName.XOBJECT)).isNotNull();
                if (texto.contains("Material de prueba")) {
                    assertThat(texto).contains("CANTIDAD", "DESCRIPCIÓN");
                }
                completo.append(texto).append('\n');
            }
            for (int i = 1; i <= 65; i++) {
                assertThat(completo.toString()).containsOnlyOnce(String.format("Material de prueba %03d", i));
            }
            assertThat(completo.toString()).containsOnlyOnce("QUIEN ENTREGA").containsOnlyOnce("QUIEN RECIBE");
        } finally {
            reader.close();
        }
    }

    @Test
    void textoLibreLargoComienzaEnPrimeraHoja() throws Exception {
        Remito remito = remito();
        String descripcion;
        try (var stream = getClass().getResourceAsStream("/presupuesto-descripcion-larga.txt")) {
            descripcion = new String(stream.readAllBytes(), StandardCharsets.UTF_8).strip();
        }
        agregar(remito, descripcion);
        byte[] pdf = generar(remito);
        Files.createDirectories(Path.of("target/pdf-verificacion"));
        Files.write(Path.of("target/pdf-verificacion/remito-texto-largo.pdf"), pdf);
        PdfReader reader = new PdfReader(pdf);
        try {
            assertThat(PdfTextExtractor.getTextFromPage(reader, 1)).contains("TRABAJO A REALIZAR");
            assertThat(PdfTextExtractor.getTextFromPage(reader, reader.getNumberOfPages())
                    .replaceAll("\\s+", " "))
                    .contains("Observaciones:", "QUIEN ENTREGA", "QUIEN RECIBE");
            StringBuilder texto = new StringBuilder();
            for (int pagina = 1; pagina <= reader.getNumberOfPages(); pagina++) {
                String contenido = PdfTextExtractor.getTextFromPage(reader, pagina);
                assertThat(contenido).contains("MOBEZA ELECTRICIDAD", "R-PRUEBA");
                texto.append(contenido).append(' ');
            }
            String normalizado = texto.toString().replaceAll("\\s+", " ");
            assertThat(normalizado).doesNotContain("(continuación)");
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
    void descripcionSinParrafosNoSeDivideEnOtroItemParaAcompanarElCierre() throws Exception {
        Remito remito = remito();
        for (int i = 1; i <= 8; i++) {
            agregar(remito, "Material previo " + i);
        }
        String descripcion;
        try (var stream = getClass().getResourceAsStream("/presupuesto-descripcion-larga.txt")) {
            descripcion = new String(stream.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("\\s+", " ").strip();
        }
        agregar(remito, descripcion);
        byte[] pdf = generar(remito);
        Files.createDirectories(Path.of("target/pdf-verificacion"));
        Files.write(Path.of("target/pdf-verificacion/remito-texto-continuo.pdf"), pdf);
        PdfReader reader = new PdfReader(pdf);
        try {
            StringBuilder texto = new StringBuilder();
            for (int pagina = 1; pagina <= reader.getNumberOfPages(); pagina++) {
                texto.append(PdfTextExtractor.getTextFromPage(reader, pagina)).append(' ');
            }
            String normalizado = texto.toString().replaceAll("\\s+", " ");
            assertThat(normalizado).doesNotContain("(continuación)")
                    .contains(descripcion);
            assertThat(remito.getItems()).hasSize(9);
            assertThat(remito.getItems().get(8).getDescripcion()).isEqualTo(descripcion);
        } finally {
            reader.close();
        }
    }

    @Test
    void cierreAprovechaElEspacioSinCrearUnaHojaExtra() throws Exception {
        Remito remito = remito();
        for (int i = 1; i <= 17; i++) {
            agregar(remito, "Material " + i);
        }
        remito.setObservaciones("Entrega completa.");
        byte[] pdf = generar(remito);
        Files.createDirectories(Path.of("target/pdf-verificacion"));
        Files.write(Path.of("target/pdf-verificacion/remito-cierre-compacto.pdf"), pdf);
        PdfReader reader = new PdfReader(pdf);
        try {
            assertThat(reader.getNumberOfPages()).isEqualTo(1);
            assertThat(PdfTextExtractor.getTextFromPage(reader, 1))
                    .contains("Material 17", "Entrega completa.", "QUIEN ENTREGA", "QUIEN RECIBE");
        } finally {
            reader.close();
        }
    }

    @Test
    void ultimoItemAcompanaLasObservacionesYFirmasSiElCierreNoCabe() throws Exception {
        Remito remito = remito();
        for (int i = 1; i <= 21; i++) {
            agregar(remito, "Material " + i);
        }
        remito.setObservaciones("Entrega completa.");
        byte[] pdf = generar(remito);
        Files.createDirectories(Path.of("target/pdf-verificacion"));
        Files.write(Path.of("target/pdf-verificacion/remito-cierre-continuado.pdf"), pdf);
        PdfReader reader = new PdfReader(pdf);
        try {
            assertThat(reader.getNumberOfPages()).isEqualTo(2);
            assertThat(PdfTextExtractor.getTextFromPage(reader, 2))
                    .contains("Material 21", "Entrega completa.", "QUIEN ENTREGA", "QUIEN RECIBE");
        } finally {
            reader.close();
        }
    }

    @Test
    void remitoCortoConservaUnaHojaConDatosYFirmas() throws Exception {
        Remito remito = remito();
        agregar(remito, "Material de prueba");
        PdfReader reader = new PdfReader(generar(remito));
        try {
            assertThat(reader.getNumberOfPages()).isEqualTo(1);
            assertThat(PdfTextExtractor.getTextFromPage(reader, 1)).contains("REMITO", "R-PRUEBA",
                    "Consumidor Final", "28/10/2026", "Material de prueba", "QUIEN ENTREGA", "QUIEN RECIBE");
        } finally {
            reader.close();
        }
    }

    private Remito remito() {
        Remito remito = new Remito();
        remito.setCodigo("R-PRUEBA");
        remito.setFechaEmision(LocalDate.of(2026, 10, 28));
        return remito;
    }

    private void agregar(Remito remito, String descripcion) {
        RemitoItem item = new RemitoItem();
        item.setDescripcion(descripcion);
        item.setCantidad(1);
        remito.agregarItem(item);
    }

    private byte[] generar(Remito remito) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new RemitoImpresionService().generarRemitoPdf(remito, out);
        return out.toByteArray();
    }
}
