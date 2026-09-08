package com.sistema.service;

import com.sistema.model.Producto;
import com.sistema.model.TipoIva;
import com.sistema.repository.MovimientoInventarioRepository;
import com.sistema.repository.ProductoRepository;
import com.sistema.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTests {
    @Mock private ProductoRepository productoRepo;
    @Mock private ProveedorRepository proveedorRepo;
    @Mock private MovimientoInventarioRepository movimientoRepo;
    @Mock private PresupuestoService presupuestoService;
    @Mock private PlatformTransactionManager transactionManager;
    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService(productoRepo, proveedorRepo, movimientoRepo,
                presupuestoService, transactionManager);
    }

    @Test
    void generaDespuesDelMayorAunqueSeHayanEliminadoProductos() {
        when(productoRepo.findSkusByPrefix("CABL"))
                .thenReturn(List.of("CABL-001", "CABL-003", "CABL-170"));
        assertThat(service.generarSku("Cable rojo")).isEqualTo("CABL-171");
    }

    @Test
    void comparaNumerosYPermiteSuperarLosTresDigitos() {
        when(productoRepo.findSkusByPrefix("CABL"))
                .thenReturn(List.of("CABL-999", "CABL-1000", "CABL-ESPECIAL", "CABL-"));
        assertThat(service.generarSku("Cable rojo")).isEqualTo("CABL-1001");
    }

    @Test
    void comienzaEnUnoParaUnPrefijoNuevo() {
        when(productoRepo.findSkusByPrefix("LED")).thenReturn(List.of());
        assertThat(service.generarSku("LED 20")).isEqualTo("LED-001");
    }

    @Test
    void reintentaLaColisionConOtraEntidadYDespuesDelRollback() {
        prepararTransacciones();
        Producto formulario = producto();
        when(productoRepo.findSkusByPrefix("CABL"))
                .thenReturn(List.of("CABL-169"))
                .thenReturn(List.of("CABL-169", "CABL-170"));
        when(productoRepo.existsBySku("CABL-170")).thenReturn(true);
        List<Producto> intentos = new ArrayList<>();
        when(productoRepo.saveAndFlush(any())).thenAnswer(invocation -> {
            Producto candidato = invocation.getArgument(0);
            intentos.add(candidato);
            assertThat(candidato.getId()).isNull();
            assertThat(formulario.getSku()).isNull();
            assertThat(formulario.getId()).isNull();
            candidato.setId(900L);
            if (intentos.size() == 1) throw duplicado();
            return candidato;
        });

        service.saveProducto(formulario);

        assertThat(intentos).hasSize(2);
        assertThat(intentos.get(0)).isNotSameAs(intentos.get(1));
        assertThat(formulario.getSku()).isEqualTo("CABL-171");
        assertThat(formulario.getId()).isEqualTo(900L);
        assertThat(formulario.getPrecioContado()).isEqualByComparingTo("123.45");
        assertThat(formulario.getCantidad()).isEqualTo(5);
        assertThat(formulario.getTipoIva()).isEqualTo(TipoIva.IVA_21);
        var orden = inOrder(transactionManager, productoRepo);
        orden.verify(productoRepo).saveAndFlush(intentos.get(0));
        orden.verify(transactionManager).rollback(any());
        orden.verify(productoRepo).existsBySku("CABL-170");
        orden.verify(productoRepo).saveAndFlush(intentos.get(1));
        orden.verify(transactionManager).commit(any());
    }

    @Test
    void dosAltasSimultaneasTerminanConSkusDistintos() throws Exception {
        prepararTransacciones();
        var existentes = new ConcurrentHashMap<String, Producto>();
        existentes.put("CABL-170", producto());
        var lecturas = new AtomicInteger();
        var primerasLecturas = new CountDownLatch(2);
        var ids = new AtomicLong(900);
        when(productoRepo.findSkusByPrefix("CABL")).thenAnswer(invocation -> {
            List<String> snapshot = new ArrayList<>(existentes.keySet());
            if (lecturas.incrementAndGet() <= 2) {
                primerasLecturas.countDown();
                assertThat(primerasLecturas.await(5, TimeUnit.SECONDS)).isTrue();
            }
            return snapshot;
        });
        when(productoRepo.existsBySku(anyString()))
                .thenAnswer(invocation -> existentes.containsKey(invocation.getArgument(0)));
        when(productoRepo.saveAndFlush(any())).thenAnswer(invocation -> {
            Producto candidato = invocation.getArgument(0);
            assertThat(candidato.getId()).isNull();
            candidato.setId(ids.incrementAndGet());
            // Simula el índice UNIQUE de MySQL y su error 1062.
            if (existentes.putIfAbsent(candidato.getSku(), candidato) != null) throw duplicado();
            return candidato;
        });
        Producto primero = producto();
        Producto segundo = producto();
        var executor = Executors.newFixedThreadPool(2);
        try {
            var alta1 = executor.submit(() -> service.saveProducto(primero));
            var alta2 = executor.submit(() -> service.saveProducto(segundo));
            alta1.get(10, TimeUnit.SECONDS);
            alta2.get(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
        assertThat(List.of(primero.getSku(), segundo.getSku()))
                .containsExactlyInAnyOrder("CABL-171", "CABL-172");
        assertThat(primero.getId()).isNotEqualTo(segundo.getId());
        verify(transactionManager, times(2)).commit(any());
        verify(transactionManager).rollback(any());
    }

    @Test
    void noReintentaOtrosErroresDeIntegridadNiModificaElFormulario() {
        prepararTransacciones();
        when(productoRepo.findSkusByPrefix("CABL")).thenReturn(List.of());
        var error = new DataIntegrityViolationException("Dato inválido",
                new SQLException("Column cannot be null", "23000", 1048));
        when(productoRepo.saveAndFlush(any())).thenThrow(error);
        Producto formulario = producto();
        assertThatThrownBy(() -> service.saveProducto(formulario)).isSameAs(error);
        assertThat(formulario.getSku()).isNull();
        assertThat(formulario.getId()).isNull();
        verify(productoRepo, times(1)).saveAndFlush(any());
        verify(productoRepo, never()).existsBySku(anyString());
        verify(transactionManager, never()).commit(any());
    }

    @Test
    void limitaLosReintentosSiPersistenLasColisiones() {
        prepararTransacciones();
        when(productoRepo.findSkusByPrefix("CABL")).thenReturn(List.of());
        when(productoRepo.existsBySku("CABL-001")).thenReturn(true);
        var error = duplicado();
        when(productoRepo.saveAndFlush(any())).thenThrow(error);
        assertThatThrownBy(() -> service.saveProducto(producto())).isSameAs(error);
        verify(productoRepo, times(10)).saveAndFlush(any());
        verify(transactionManager, times(10)).rollback(any());
    }

    @Test
    void conservaUnSkuExplicitoYNoLoReemplazaSiEstaDuplicado() {
        prepararTransacciones();
        Producto formulario = producto();
        formulario.setSku("MANUAL-123");
        var error = duplicado();
        when(productoRepo.saveAndFlush(any())).thenThrow(error);
        assertThatThrownBy(() -> service.saveProducto(formulario)).isSameAs(error);
        assertThat(formulario.getSku()).isEqualTo("MANUAL-123");
        verify(productoRepo, never()).findSkusByPrefix(anyString());
        verify(productoRepo, times(1)).saveAndFlush(any());
    }

    private void prepararTransacciones() {
        when(transactionManager.getTransaction(any())).thenAnswer(invocation -> {
            TransactionDefinition definition = invocation.getArgument(0);
            assertThat(definition.getPropagationBehavior())
                    .isEqualTo(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            return new SimpleTransactionStatus();
        });
    }

    private static DataIntegrityViolationException duplicado() {
        return new DataIntegrityViolationException("SKU duplicado",
                new SQLException("Duplicate entry", "23000", 1062));
    }

    private static Producto producto() {
        Producto producto = new Producto();
        producto.setDescripcion("Cable rojo");
        producto.setCantidad(5);
        producto.setPrecioContado(new BigDecimal("123.45"));
        producto.setTipoIva(TipoIva.IVA_21);
        return producto;
    }
}
