package com.sistema.service;

import com.sistema.model.EstadoPresupuesto;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresupuestoEliminarServiceTests {

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
    void eliminaUnPresupuestoPendienteSinVenta() {
        Presupuesto presupuesto = presupuesto(
                EstadoPresupuesto.PENDIENTE);
        when(presupuestoRepo.findByIdConDetalles(10L))
                .thenReturn(Optional.of(presupuesto));
        when(ventaRepo.findAllByPresupuestoCodigo("0010"))
                .thenReturn(List.of());

        service.eliminar(10L);

        verify(presupuestoRepo).delete(presupuesto);
    }

    @Test
    void noEliminaUnPresupuestoConVentaAsociada() {
        Presupuesto presupuesto = presupuesto(
                EstadoPresupuesto.PENDIENTE);
        when(presupuestoRepo.findByIdConDetalles(10L))
                .thenReturn(Optional.of(presupuesto));
        when(ventaRepo.findAllByPresupuestoCodigo("0010"))
                .thenReturn(List.of(new Venta()));

        assertThatThrownBy(() -> service.eliminar(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("venta asociada");

        verify(presupuestoRepo, never()).delete(presupuesto);
    }

    @Test
    void noEliminaUnPresupuestoAprobado() {
        Presupuesto presupuesto = presupuesto(
                EstadoPresupuesto.APROBADO);
        when(presupuestoRepo.findByIdConDetalles(10L))
                .thenReturn(Optional.of(presupuesto));

        assertThatThrownBy(() -> service.eliminar(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pendiente o rechazado");

        verify(presupuestoRepo, never()).delete(presupuesto);
    }

    private Presupuesto presupuesto(EstadoPresupuesto estado) {
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(10L);
        presupuesto.setCodigo("0010");
        presupuesto.setEstado(estado);
        return presupuesto;
    }
}
