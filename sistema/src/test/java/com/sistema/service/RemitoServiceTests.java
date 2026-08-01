package com.sistema.service;

import com.sistema.model.Cliente;
import com.sistema.model.FormaPago;
import com.sistema.model.Producto;
import com.sistema.model.Remito;
import com.sistema.model.RemitoItem;
import com.sistema.model.TipoIva;
import com.sistema.model.Venta;
import com.sistema.repository.ProductoRepository;
import com.sistema.repository.RemitoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemitoServiceTests {

    @Mock
    private RemitoRepository remitoRepo;
    @Mock
    private ProductoRepository productoRepo;
    @Mock
    private MovimientoInventarioService movimientoService;
    @Mock
    private VentaService ventaService;

    private RemitoService service;

    @BeforeEach
    void setUp() {
        service = new RemitoService(
                remitoRepo,
                productoRepo,
                movimientoService,
                ventaService);
        when(remitoRepo.save(any(Remito.class))).thenAnswer(invocation -> {
            Remito remito = invocation.getArgument(0);
            if (remito.getId() == null) {
                remito.setId(1L);
            }
            return remito;
        });
    }

    @Test
    void crearRemitoDescuentaStockUnaSolaVezYQuedaPendienteDeCobro() {
        Cliente cliente = new Cliente();
        cliente.setId(10L);
        Producto producto = producto(20L);

        when(remitoRepo.findMaxId()).thenReturn(4L);
        when(productoRepo.findById(20L)).thenReturn(Optional.of(producto));

        Remito remito = service.crearCuentaCorriente(
                cliente,
                List.of(20L),
                List.of(2),
                List.of(new BigDecimal("150.00")),
                List.of(new BigDecimal("10.00")),
                FormaPago.CUENTA_CORRIENTE,
                "Dirección de prueba",
                "Cuenta corriente");

        assertThat(remito.getCodigo()).isEqualTo("0005");
        assertThat(remito.getEstado()).isEqualTo(Remito.Estado.ENTREGADO);
        assertThat(remito.getStockDescontado()).isTrue();
        assertThat(remito.getFormaPagoPrecio())
                .isEqualTo(FormaPago.CUENTA_CORRIENTE);
        assertThat(remito.getTotal()).isEqualByComparingTo("270.00");
        verify(movimientoService).registrarVenta(
                20L, 2, "Entrega por remito 0005");
        verify(movimientoService, never()).registrarDevolucion(
                any(), any(), any());
    }

    @Test
    void crearRemitoSinClienteLoDejaComoConsumidorFinal() {
        Producto producto = producto(30L);
        when(remitoRepo.findMaxId()).thenReturn(5L);
        when(productoRepo.findById(30L)).thenReturn(Optional.of(producto));

        Remito remito = service.crearCuentaCorriente(
                null,
                List.of(30L),
                List.of(1),
                List.of(new BigDecimal("120.00")),
                List.of(BigDecimal.ZERO),
                FormaPago.CONTADO,
                null,
                null);

        assertThat(remito.getCliente()).isNull();
        assertThat(remito.getFormaPagoPrecio()).isEqualTo(FormaPago.CONTADO);
        assertThat(remito.getTotal()).isEqualByComparingTo("120.00");
        verify(movimientoService).registrarVenta(
                30L, 1, "Entrega por remito 0006");
    }

    @Test
    void cobrarRemitoCreaVentaSinVolverADescontarStock() {
        Remito remito = remitoEntregado();
        Venta venta = new Venta();
        venta.setId(99L);

        when(remitoRepo.findById(1L)).thenReturn(Optional.of(remito));
        when(ventaService.crearDesdeRemito(remito, FormaPago.CONTADO))
                .thenReturn(venta);

        Venta resultado = service.convertirAVenta(1L, FormaPago.CONTADO);

        assertThat(resultado.getId()).isEqualTo(99L);
        assertThat(remito.getEstado()).isEqualTo(Remito.Estado.CONVERTIDO);
        assertThat(remito.getVenta()).isSameAs(venta);
        verify(movimientoService, never()).registrarVenta(
                any(), any(), any());
        verify(movimientoService, never()).registrarDevolucion(
                any(), any(), any());
    }

    @Test
    void anularRemitoPendienteDevuelveElStock() {
        Remito remito = remitoEntregado();
        when(remitoRepo.findById(1L)).thenReturn(Optional.of(remito));

        service.anular(1L);

        assertThat(remito.getEstado()).isEqualTo(Remito.Estado.ANULADO);
        assertThat(remito.getStockDescontado()).isFalse();
        verify(movimientoService).registrarDevolucion(
                20L, 2, "Anulación remito 0001");
    }

    private Remito remitoEntregado() {
        Remito remito = new Remito();
        remito.setId(1L);
        remito.setCodigo("0001");
        remito.setEstado(Remito.Estado.ENTREGADO);
        remito.setStockDescontado(true);

        RemitoItem item = new RemitoItem();
        item.setProducto(producto(20L));
        item.setCantidad(2);
        item.setPrecioUnitario(new BigDecimal("150.00"));
        item.calcularSubtotal();
        remito.agregarItem(item);
        remito.calcularTotal();
        return remito;
    }

    private Producto producto(Long id) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setSku("PROD-001");
        producto.setDescripcion("Producto de prueba");
        producto.setCantidad(10);
        producto.setPrecioCompra(new BigDecimal("100.00"));
        producto.setPrecioCuentaCorriente(new BigDecimal("150.00"));
        producto.setTipoIva(TipoIva.IVA_21);
        return producto;
    }
}
