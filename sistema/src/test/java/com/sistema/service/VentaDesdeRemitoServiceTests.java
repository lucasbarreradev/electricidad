package com.sistema.service;

import com.sistema.model.Cliente;
import com.sistema.model.FormaPago;
import com.sistema.model.Producto;
import com.sistema.model.Remito;
import com.sistema.model.RemitoItem;
import com.sistema.model.TipoIva;
import com.sistema.model.Venta;
import com.sistema.repository.ClienteRepository;
import com.sistema.repository.PresupuestoRepository;
import com.sistema.repository.ProductoRepository;
import com.sistema.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaDesdeRemitoServiceTests {

    @Mock
    private VentaRepository ventaRepo;
    @Mock
    private ProductoRepository productoRepo;
    @Mock
    private ClienteRepository clienteRepo;
    @Mock
    private PresupuestoRepository presupuestoRepo;
    @Mock
    private MovimientoInventarioService movimientoService;

    private VentaService service;

    @BeforeEach
    void setUp() {
        service = new VentaService(
                ventaRepo,
                productoRepo,
                clienteRepo,
                presupuestoRepo,
                movimientoService);
    }

    @Test
    void creaVentaConPrecioDelRemitoSinMovimientoDeInventario() {
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setPrecioCompra(new BigDecimal("80.00"));
        producto.setTipoIva(TipoIva.IVA_21);

        Cliente cliente = new Cliente();
        cliente.setId(7L);

        Remito remito = new Remito();
        remito.setCodigo("0010");
        remito.setCliente(cliente);

        RemitoItem remitoItem = new RemitoItem();
        remitoItem.setProducto(producto);
        remitoItem.setCantidad(2);
        remitoItem.setPrecioUnitario(new BigDecimal("121.00"));
        remitoItem.setDescuentoPct(new BigDecimal("10.00"));
        remitoItem.calcularSubtotal();
        remito.agregarItem(remitoItem);

        when(productoRepo.findById(5L)).thenReturn(Optional.of(producto));
        when(ventaRepo.save(any(Venta.class))).thenAnswer(invocation -> {
            Venta venta = invocation.getArgument(0);
            venta.setId(50L);
            return venta;
        });

        Venta venta = service.crearDesdeRemito(remito, FormaPago.CONTADO);

        assertThat(venta.getId()).isEqualTo(50L);
        assertThat(venta.getOrigen()).isEqualTo(Venta.Origen.DIRECTA);
        assertThat(venta.getTotal()).isEqualByComparingTo("217.80");
        assertThat(venta.getItems()).hasSize(1);
        assertThat(venta.getItems().get(0).getPrecioUnitario())
                .isEqualByComparingTo("121.00");
        assertThat(venta.getItems().get(0).getDescuentoPct())
                .isEqualByComparingTo("10.00");
        verifyNoInteractions(movimientoService);
    }
}
