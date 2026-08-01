package com.sistema.service;

import com.sistema.model.DetallePresupuesto;
import com.sistema.model.EstadoPresupuesto;
import com.sistema.model.FormaPago;
import com.sistema.model.Presupuesto;
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
class VentaDesdePresupuestoManualServiceTests {

    @Mock private VentaRepository ventaRepo;
    @Mock private ProductoRepository productoRepo;
    @Mock private ClienteRepository clienteRepo;
    @Mock private PresupuestoRepository presupuestoRepo;
    @Mock private MovimientoInventarioService movimientoService;

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
    void convierteItemManualAVentaSinDescontarStock() {
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(12L);
        presupuesto.setCodigo("0012");
        presupuesto.setEstado(EstadoPresupuesto.PENDIENTE);

        DetallePresupuesto detalle = new DetallePresupuesto();
        detalle.setDescripcion("Trabajo eléctrico especial");
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("121.00"));
        detalle.setDescuentoPct(BigDecimal.ZERO);
        detalle.setAlicuotaIva(new BigDecimal("21.00"));
        detalle.calcularSubtotal();
        presupuesto.agregarDetalle(detalle);

        when(presupuestoRepo.findById(12L)).thenReturn(Optional.of(presupuesto));
        when(ventaRepo.save(any(Venta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Venta venta = service.crearDesdePresupuesto(12L, FormaPago.CONTADO);

        assertThat(venta.getItems()).hasSize(1);
        assertThat(venta.getItems().get(0).getProducto()).isNull();
        assertThat(venta.getItems().get(0).getDescripcionMostrada())
                .isEqualTo("Trabajo eléctrico especial");
        assertThat(venta.getTotal()).isEqualByComparingTo("242.00");
        assertThat(presupuesto.getEstado()).isEqualTo(EstadoPresupuesto.VENDIDO);
        verifyNoInteractions(productoRepo, clienteRepo, movimientoService);
    }
}
