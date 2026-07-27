package com.sistema.service;

import com.sistema.model.Producto;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoExcelService {

    private static final String[] COLUMNAS = {
            "SKU",
            "Descripción",
            "Cantidad",
            "Precio de compra",
            "Precio contado",
            "Precio cuenta corriente",
            "Precio tarjeta",
            "IVA",
            "Proveedor"
    };

    public void exportar(List<Producto> productos, OutputStream outputStream)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Productos");
            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(new CellRangeAddress(
                    0, 0, 0, COLUMNAS.length - 1));

            CellStyle encabezadoStyle = crearEstiloEncabezado(workbook);
            CellStyle enteroStyle = crearEstiloNumero(workbook, "#,##0");
            CellStyle monedaStyle = crearEstiloNumero(workbook, "$ #,##0.00");

            Row encabezado = sheet.createRow(0);
            encabezado.setHeightInPoints(24);
            for (int columna = 0; columna < COLUMNAS.length; columna++) {
                Cell cell = encabezado.createCell(columna);
                cell.setCellValue(COLUMNAS[columna]);
                cell.setCellStyle(encabezadoStyle);
            }

            int numeroFila = 1;
            for (Producto producto : productos) {
                Row fila = sheet.createRow(numeroFila++);

                escribirTexto(fila, 0, producto.getSku());
                escribirTexto(fila, 1, producto.getDescripcion());
                escribirEntero(fila, 2, producto.getCantidad(), enteroStyle);
                escribirDecimal(fila, 3, producto.getPrecioCompra(), monedaStyle);
                escribirDecimal(fila, 4, producto.getPrecioContado(), monedaStyle);
                escribirDecimal(
                        fila, 5, producto.getPrecioCuentaCorriente(), monedaStyle);
                escribirDecimal(fila, 6, producto.getPrecioTarjeta(), monedaStyle);
                escribirTexto(
                        fila,
                        7,
                        producto.getTipoIva() == null
                                ? ""
                                : producto.getTipoIva().getDescripcion());
                escribirTexto(
                        fila,
                        8,
                        producto.getProveedor() == null
                                ? "Sin proveedor"
                                : producto.getProveedor().getNombreRazonSocial());
            }

            ajustarAnchos(sheet);
            workbook.write(outputStream);
        }
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloNumero(Workbook workbook, String formato) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(formato));
        return style;
    }

    private void escribirTexto(Row fila, int columna, String valor) {
        fila.createCell(columna).setCellValue(valor == null ? "" : valor);
    }

    private void escribirEntero(
            Row fila, int columna, Integer valor, CellStyle style) {

        Cell cell = fila.createCell(columna);
        if (valor != null) {
            cell.setCellValue(valor);
        }
        cell.setCellStyle(style);
    }

    private void escribirDecimal(
            Row fila, int columna, BigDecimal valor, CellStyle style) {

        Cell cell = fila.createCell(columna);
        if (valor != null) {
            cell.setCellValue(valor.doubleValue());
        }
        cell.setCellStyle(style);
    }

    private void ajustarAnchos(Sheet sheet) {
        int[] anchos = {16, 42, 12, 18, 18, 24, 18, 14, 32};
        for (int columna = 0; columna < anchos.length; columna++) {
            sheet.setColumnWidth(columna, anchos[columna] * 256);
        }
    }
}
