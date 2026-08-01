package com.sistema.service;

import com.sistema.model.FormaPago;
import com.sistema.model.Presupuesto;
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
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresupuestoManualServiceTests {

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
        when(presupuestoRepo.save(any(Presupuesto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void creaPresupuestoConItemManualSinBuscarProducto() {
        Presupuesto presupuesto = service.crear(
                null,
                FormaPago.CONTADO,
                List.of(0L),
                List.of(2),
                List.of(new BigDecimal("10.00")),
                LocalDate.now().plusDays(15),
                Presupuesto.Moneda.ARS,
                null,
                null,
                List.of(new BigDecimal("150.00")),
                List.of("Instalación y materiales varios"),
                List.of(new BigDecimal("21.00")));

        assertThat(presupuesto.getDetalles()).hasSize(1);
        assertThat(presupuesto.getDetalles().get(0).getProducto()).isNull();
        assertThat(presupuesto.getDetalles().get(0).getDescripcionMostrada())
                .isEqualTo("Instalación y materiales varios");
        assertThat(presupuesto.getDetalles().get(0).getAlicuotaIva())
                .isEqualByComparingTo("21.00");
        assertThat(presupuesto.getTotal()).isEqualByComparingTo("270.00");
        verifyNoInteractions(productoRepo, clienteRepo, movimientoService);
    }
}
