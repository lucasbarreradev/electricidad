package com.sistema.service;

import com.sistema.model.Producto;
import com.sistema.model.TipoIva;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductoExcelServiceTests {

    private final ProductoExcelService service = new ProductoExcelService();

    @Test
    void exportaTodosLosProductosEnUnXlsxValido() throws Exception {
        Producto producto = new Producto();
        producto.setSku("PROD-001");
        producto.setDescripcion("Producto de prueba");
        producto.setCantidad(12);
        producto.setPrecioCompra(new BigDecimal("100.50"));
        producto.setPrecioContado(new BigDecimal("150.75"));
        producto.setPrecioCuentaCorriente(new BigDecimal("170.00"));
        producto.setPrecioTarjeta(new BigDecimal("180.25"));
        producto.setTipoIva(TipoIva.IVA_21);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        service.exportar(List.of(producto), salida);

        try (XSSFWorkbook workbook = new XSSFWorkbook(
                new ByteArrayInputStream(salida.toByteArray()))) {

            Sheet sheet = workbook.getSheet("Productos");

            assertThat(sheet).isNotNull();
            assertThat(sheet.getLastRowNum()).isEqualTo(1);
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue())
                    .isEqualTo("SKU");
            assertThat(sheet.getRow(1).getCell(0).getStringCellValue())
                    .isEqualTo("PROD-001");
            assertThat(sheet.getRow(1).getCell(2).getCellType())
                    .isEqualTo(CellType.NUMERIC);
            assertThat(sheet.getRow(1).getCell(4).getNumericCellValue())
                    .isEqualTo(150.75);
            assertThat(sheet.getRow(1).getCell(7).getStringCellValue())
                    .isEqualTo("IVA 21%");
            assertThat(sheet.getRow(1).getCell(8).getStringCellValue())
                    .isEqualTo("Sin proveedor");
        }
    }
}
