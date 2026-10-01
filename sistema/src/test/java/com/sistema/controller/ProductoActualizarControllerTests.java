package com.sistema.controller;

import com.sistema.model.Producto;
import com.sistema.service.ProductoExcelService;
import com.sistema.service.ProductoService;
import com.sistema.service.ProveedorService;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class ProductoActualizarControllerTests {

    @Test
    void muestraUnMensajeEntendibleSiFallaLaActualizacion() {
        ProductoService service = mock(ProductoService.class);
        ProductoController controller = new ProductoController(service,
                mock(ProveedorService.class), mock(ProductoExcelService.class));
        Producto producto = new Producto();
        var atributos = new RedirectAttributesModelMap();
        doThrow(new NullPointerException("detalle interno"))
                .when(service).updateProducto(61L, producto);

        String destino = controller.actualizar(61L, producto, atributos);

        assertThat(destino).isEqualTo("redirect:/productos/editar/61");
        assertThat(atributos.getFlashAttributes().get("error")).isEqualTo(
                "No se pudo actualizar el producto. Verificá los precios ingresados.");
    }
}
