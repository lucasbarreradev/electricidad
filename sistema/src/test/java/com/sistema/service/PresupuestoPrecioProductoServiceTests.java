package com.sistema.service;

import com.sistema.model.DetallePresupuesto;
import com.sistema.model.EstadoPresupuesto;
import com.sistema.model.FormaPago;
import com.sistema.model.Presupuesto;
import com.sistema.model.Producto;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresupuestoPrecioProductoServiceTests {

    @Mock private PresupuestoRepository presupuestoRepo;
    @Mock private ProductoRepository productoRepo;
    @Mock private ClienteRepository clienteRepo;
    @Mock private VentaService ventaService;
    @Mock private VentaRepository ventaRepo;
    @Mock private MovimientoInventarioService movimientoService;

    private PresupuestoService service;

    @BeforeEach
    void setUp() {
        service = new PresupuestoService(
                presupuestoRepo,
                productoRepo,
                clienteRepo,
                ventaService,
                ventaRepo,
                movimientoService);
    }

    @Test
    void conservaElPrecioDelPresupuestoSiElNuevoPrecioEstaVacio() {
        Presupuesto presupuesto = presupuestoPendiente(FormaPago.CONTADO);
        DetallePresupuesto detalle = presupuesto.getDetalles().get(0);
        when(presupuestoRepo.findByEstado(EstadoPresupuesto.PENDIENTE))
                .thenReturn(List.of(presupuesto));

        service.actualizarPrecioProductoEnPendientes(61L, null, null, null);

        assertThat(detalle.getPrecioUnitario()).isEqualByComparingTo("1500.00");
        assertThat(detalle.getSubtotal()).isEqualByComparingTo("3000.00");
        verify(presupuestoRepo, never()).save(presupuesto);
    }

    @Test
    void actualizaYRecalculaCuandoHayUnNuevoPrecio() {
        Presupuesto presupuesto = presupuestoPendiente(FormaPago.CONTADO);
        DetallePresupuesto detalle = presupuesto.getDetalles().get(0);
        when(presupuestoRepo.findByEstado(EstadoPresupuesto.PENDIENTE))
                .thenReturn(List.of(presupuesto));

        service.actualizarPrecioProductoEnPendientes(
                61L, new BigDecimal("1750.00"), null, null);

        assertThat(detalle.getPrecioUnitario()).isEqualByComparingTo("1750.00");
        assertThat(detalle.getSubtotal()).isEqualByComparingTo("3500.00");
        verify(presupuestoRepo).save(presupuesto);
    }

    private Presupuesto presupuestoPendiente(FormaPago formaPago) {
        Producto producto = new Producto();
        producto.setId(61L);

        DetallePresupuesto detalle = new DetallePresupuesto();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("1500.00"));
        detalle.setDescuentoPct(BigDecimal.ZERO);
        detalle.setAlicuotaIva(new BigDecimal("21.00"));
        detalle.calcularSubtotal();

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setEstado(EstadoPresupuesto.PENDIENTE);
        presupuesto.setFormaPago(formaPago);
        presupuesto.agregarDetalle(detalle);
        presupuesto.calcularTotal();
        return presupuesto;
    }
}
