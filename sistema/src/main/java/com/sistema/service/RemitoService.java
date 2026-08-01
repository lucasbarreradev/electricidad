package com.sistema.service;

import com.sistema.model.Cliente;
import com.sistema.model.FormaPago;
import com.sistema.model.Producto;
import com.sistema.model.Remito;
import com.sistema.model.RemitoItem;
import com.sistema.model.Venta;
import com.sistema.model.VentaItem;
import com.sistema.repository.ProductoRepository;
import com.sistema.repository.RemitoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RemitoService {

    private final RemitoRepository remitoRepo;
    private final ProductoRepository productoRepo;
    private final MovimientoInventarioService movimientoService;
    private final VentaService ventaService;

    public RemitoService(RemitoRepository remitoRepo,
                         ProductoRepository productoRepo,
                         MovimientoInventarioService movimientoService,
                         VentaService ventaService) {
        this.remitoRepo = remitoRepo;
        this.productoRepo = productoRepo;
        this.movimientoService = movimientoService;
        this.ventaService = ventaService;
    }

    /**
     * Crea un remito. La mercadería se considera entregada
     * en el momento de guardarlo, por lo que el stock se descuenta dentro de la
     * misma transacción.
     */
    public Remito crearCuentaCorriente(Cliente cliente,
                                       List<Long> productoIds,
                                       List<Integer> cantidades,
                                       List<BigDecimal> precios,
                                       List<BigDecimal> descuentos,
                                       FormaPago formaPagoPrecio,
                                       String direccionEntrega,
                                       String observaciones) {

        validarDatosCreacion(
                productoIds, cantidades, precios, descuentos);
        if (formaPagoPrecio == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una forma de pago para calcular los precios");
        }

        Remito remito = new Remito();
        remito.setCliente(cliente);
        remito.setDireccionEntrega(direccionEntrega);
        remito.setObservaciones(observaciones);
        remito.setIncluyePrecios(true);
        remito.setEstado(Remito.Estado.ENTREGADO);
        remito.setTipo(Remito.Tipo.ENTREGA);
        remito.setFormaPagoPrecio(formaPagoPrecio);
        remito.setStockDescontado(false);
        remito.setCodigo(generarCodigo());

        for (int i = 0; i < productoIds.size(); i++) {
            Producto producto = productoRepo.findById(productoIds.get(i))
                    .orElseThrow(() ->
                            new IllegalArgumentException("Producto no encontrado"));

            Integer cantidad = cantidades.get(i);
            BigDecimal precio = precios.get(i);
            BigDecimal descuento = descuentos.get(i);

            if (cantidad == null || cantidad <= 0) {
                throw new IllegalArgumentException(
                        "Todas las cantidades deben ser mayores a cero");
            }
            if (precio == null || precio.signum() < 0) {
                throw new IllegalArgumentException(
                        "Todos los precios deben ser válidos");
            }
            if (descuento == null
                    || descuento.signum() < 0
                    || descuento.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException(
                        "Los descuentos deben estar entre 0 y 100");
            }

            RemitoItem item = new RemitoItem();
            item.setProducto(producto);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(precio);
            item.setDescuentoPct(descuento);
            item.calcularSubtotal();
            remito.agregarItem(item);
        }

        remito.calcularTotal();
        Remito guardado = remitoRepo.save(remito);

        descontarStockRemito(guardado);
        guardado.setStockDescontado(true);
        return remitoRepo.save(guardado);
    }

    /**
     * Compatibilidad con remitos pendientes anteriores. Al entregarlos, la
     * salida de stock se registra exactamente una vez.
     */
    public void marcarComoEntregado(Long remitoId) {
        Remito remito = buscarPorId(remitoId);

        if (remito.getEstado() != Remito.Estado.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden entregar remitos pendientes");
        }

        if (!Boolean.TRUE.equals(remito.getStockDescontado())) {
            descontarStockRemito(remito);
            remito.setStockDescontado(true);
        }

        remito.setEstado(Remito.Estado.ENTREGADO);
        remitoRepo.save(remito);
    }

    /**
     * Registra el pago creando la venta. No vuelve a descontar stock porque la
     * mercadería salió al momento de crear/entregar el remito.
     */
    public Venta convertirAVenta(Long remitoId, FormaPago formaPago) {
        Remito remito = buscarPorId(remitoId);

        if (remito.getEstado() == Remito.Estado.CONVERTIDO) {
            throw new IllegalStateException(
                    "Este remito ya fue convertido a venta");
        }
        if (remito.getEstado() == Remito.Estado.ANULADO) {
            throw new IllegalStateException(
                    "No se puede cobrar un remito anulado");
        }
        if (remito.getEstado() != Remito.Estado.ENTREGADO) {
            throw new IllegalStateException(
                    "El remito debe estar entregado antes de cobrarlo");
        }

        // Protege remitos antiguos que no registraban correctamente la salida.
        if (!Boolean.TRUE.equals(remito.getStockDescontado())) {
            descontarStockRemito(remito);
            remito.setStockDescontado(true);
        }

        Venta venta = ventaService.crearDesdeRemito(remito, formaPago);

        remito.setEstado(Remito.Estado.CONVERTIDO);
        remito.setVenta(venta);
        remito.setFechaConversion(LocalDate.now());
        remitoRepo.save(remito);

        return venta;
    }

    public void anular(Long remitoId) {
        Remito remito = buscarPorId(remitoId);

        if (remito.getEstado() == Remito.Estado.CONVERTIDO) {
            throw new IllegalStateException(
                    "No se puede anular un remito que ya fue cobrado");
        }
        if (remito.getEstado() == Remito.Estado.ANULADO) {
            throw new IllegalStateException("El remito ya está anulado");
        }

        if (Boolean.TRUE.equals(remito.getStockDescontado())) {
            devolverStockRemito(remito);
            remito.setStockDescontado(false);
        }

        remito.setEstado(Remito.Estado.ANULADO);
        remitoRepo.save(remito);
    }

    private void descontarStockRemito(Remito remito) {
        for (RemitoItem item : remito.getItems()) {
            if (item.getProducto() == null) {
                continue;
            }
            movimientoService.registrarVenta(
                    item.getProducto().getId(),
                    item.getCantidad(),
                    "Entrega por remito " + remito.getCodigo());
        }
    }

    private void devolverStockRemito(Remito remito) {
        for (RemitoItem item : remito.getItems()) {
            if (item.getProducto() == null) {
                continue;
            }
            movimientoService.registrarDevolucion(
                    item.getProducto().getId(),
                    item.getCantidad(),
                    "Anulación remito " + remito.getCodigo());
        }
    }

    public Remito crearDesdeVenta(Venta venta) {
        Remito remito = new Remito();
        remito.setCliente(venta.getCliente());
        remito.setEstado(Remito.Estado.CONVERTIDO);
        remito.setTipo(Remito.Tipo.ENTREGA);
        remito.setIncluyePrecios(true);
        remito.setStockDescontado(true);
        remito.setFormaPagoPrecio(venta.getFormaPago());
        remito.setFechaConversion(LocalDate.now());
        remito.setObservaciones(
                "Generado desde venta " + venta.getCodigo());
        remito.setCodigo(generarCodigo());

        BigDecimal total = BigDecimal.ZERO;

        for (VentaItem ventaItem : venta.getItems()) {
            RemitoItem remitoItem = new RemitoItem();
            remitoItem.setProducto(ventaItem.getProducto());
            remitoItem.setDescripcion(ventaItem.getDescripcionMostrada());
            remitoItem.setCantidad(ventaItem.getCantidad());
            remitoItem.setPrecioUnitario(ventaItem.getPrecioUnitario());
            remitoItem.setDescuentoPct(ventaItem.getDescuentoPct());
            remitoItem.calcularSubtotal();

            remito.agregarItem(remitoItem);
            total = total.add(remitoItem.getSubtotal());
        }

        remito.setTotal(total);
        remito.setVenta(venta);
        return remitoRepo.save(remito);
    }

    private void validarDatosCreacion(List<Long> productoIds,
                                      List<Integer> cantidades,
                                      List<BigDecimal> precios,
                                      List<BigDecimal> descuentos) {
        if (productoIds == null || productoIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe agregar al menos un producto");
        }
        if (cantidades == null || precios == null || descuentos == null
                || productoIds.size() != cantidades.size()
                || productoIds.size() != precios.size()
                || productoIds.size() != descuentos.size()) {
            throw new IllegalArgumentException(
                    "Los datos de los productos son inválidos");
        }

        Set<Long> productosUnicos = new HashSet<>(productoIds);
        if (productosUnicos.size() != productoIds.size()) {
            throw new IllegalArgumentException(
                    "No se puede agregar el mismo producto más de una vez");
        }
    }

    private String generarCodigo() {
        Long maxId = remitoRepo.findMaxId();
        long siguiente = (maxId != null ? maxId : 0L) + 1;
        return String.format("%04d", siguiente);
    }

    public Remito buscarPorVentaId(Long ventaId) {
        return remitoRepo.findByVentaId(ventaId).stream()
                .findFirst()
                .map(this::normalizarRemitoVinculadoAVenta)
                .orElse(null);
    }

    public List<Remito> listarTodos() {
        List<Remito> remitos = remitoRepo.findAllByOrderByFechaEmisionDesc();
        remitos.forEach(this::normalizarRemitoVinculadoAVenta);
        return remitos;
    }

    public List<Remito> listarPorEstado(Remito.Estado estado) {
        return listarTodos().stream()
                .filter(remito -> remito.getEstado() == estado)
                .collect(Collectors.toList());
    }

    public List<Remito> listarPorCliente(Long clienteId) {
        List<Remito> remitos =
                remitoRepo.findByClienteIdOrderByFechaEmisionDesc(clienteId);
        remitos.forEach(this::normalizarRemitoVinculadoAVenta);
        return remitos;
    }

    public Remito buscarPorId(Long id) {
        return remitoRepo.findById(id)
                .map(this::normalizarRemitoVinculadoAVenta)
                .orElseThrow(() ->
                        new IllegalArgumentException("Remito no encontrado"));
    }

    /**
     * Los remitos creados desde una venta por versiones anteriores quedaban
     * como ENTREGADO. Al cargarlos se corrige el estado porque esa venta ya
     * descontó el stock y no representa una deuda pendiente.
     */
    private Remito normalizarRemitoVinculadoAVenta(Remito remito) {
        if (remito.getVenta() != null
                && remito.getEstado() != Remito.Estado.ANULADO) {
            boolean modificado = false;

            if (remito.getEstado() != Remito.Estado.CONVERTIDO) {
                remito.setEstado(Remito.Estado.CONVERTIDO);
                modificado = true;
            }
            if (!Boolean.TRUE.equals(remito.getStockDescontado())) {
                remito.setStockDescontado(true);
                modificado = true;
            }
            if (remito.getFechaConversion() == null) {
                remito.setFechaConversion(remito.getFechaEmision());
                modificado = true;
            }

            if (modificado) {
                return remitoRepo.save(remito);
            }
        }
        return remito;
    }
}
