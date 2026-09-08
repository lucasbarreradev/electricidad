package com.sistema.controller;

import com.sistema.model.Producto;
import com.sistema.service.ProductoExcelService;
import com.sistema.service.ProductoService;
import com.sistema.service.ProveedorService;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.support.SimpleSessionStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductoControllerTests {
    @Test
    void unAltaDescartaElIdYSkuQueQuedaronEnLaSesion() {
        ProductoService service = mock(ProductoService.class);
        ProductoController controller = new ProductoController(service,
                mock(ProveedorService.class), mock(ProductoExcelService.class));
        Producto producto = new Producto();
        producto.setId(716L);
        producto.setSku("CABL-170");
        producto.setDescripcion("Cable nuevo");
        var status = new SimpleSessionStatus();
        doAnswer(invocation -> {
            Producto recibido = invocation.getArgument(0);
            assertThat(recibido.getId()).isNull();
            assertThat(recibido.getSku()).isNull();
            assertThat(recibido.getDescripcion()).isEqualTo("Cable nuevo");
            return null;
        }).when(service).saveProducto(producto);

        assertThat(controller.guardar(producto, null, status)).isEqualTo("redirect:/productos");
        assertThat(status.isComplete()).isTrue();
        verify(service).saveProducto(producto);
    }
}
