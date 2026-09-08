package com.sistema.service;

import com.sistema.model.Producto;
import com.sistema.model.Proveedor;
import com.sistema.repository.MovimientoInventarioRepository;
import com.sistema.repository.ProductoRepository;
import com.sistema.repository.ProveedorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ProductoService {
    private static final int MAX_INTENTOS_SKU = 10;
    private final ProductoRepository productoRepo;
    private final ProveedorRepository proveedorRepository;
    private final MovimientoInventarioRepository movimientoRepo;
    private final PresupuestoService presupuestoService;
    private final TransactionTemplate guardadoTransaction;

    public ProductoService(ProductoRepository productoRepo,
                            ProveedorRepository proveedorRepository,
                           MovimientoInventarioRepository movimientoRepo,
                           PresupuestoService presupuestoService,
                           PlatformTransactionManager transactionManager) {
        this.productoRepo = productoRepo;
        this.proveedorRepository = proveedorRepository;
        this.movimientoRepo = movimientoRepo;
        this.presupuestoService = presupuestoService;
        this.guardadoTransaction = new TransactionTemplate(transactionManager);
        this.guardadoTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public List<Producto> getProductos() {
        return productoRepo.findAllByOrderByDescripcionAsc();
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepo.findById(id);
    }

    public String generarSku(String descripcion) {

        String base = descripcion
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z]", "");

        String prefijo = base.substring(0, Math.min(4, base.length()));

        BigInteger siguiente = productoRepo.findSkusByPrefix(prefijo).stream()
                .map(sku -> sku.substring(prefijo.length() + 1))
                .filter(numero -> numero.matches("[0-9]+"))
                .map(BigInteger::new)
                .max(BigInteger::compareTo)
                .orElse(BigInteger.ZERO)
                .add(BigInteger.ONE);

        return prefijo + "-" + String.format(Locale.ROOT, "%03d", siguiente);
    }



    public void saveProducto(Producto producto) {
        boolean generarAutomaticamente = producto.getSku() == null || producto.getSku().isBlank();

        for (int intento = 1; ; intento++) {
            // Un INSERT fallido puede dejar un ID asignado en la entidad.
            // Cada intento usa una copia y una transacción independiente.
            Producto candidato = new Producto();
            BeanUtils.copyProperties(producto, candidato);
            try {
                Producto guardado = guardadoTransaction.execute(status -> {
                    if (candidato.getProveedor() != null && candidato.getProveedor().getId() != null) {
                        candidato.setProveedor(proveedorRepository
                                .findById(candidato.getProveedor().getId()).orElse(null));
                    }
                    if (generarAutomaticamente) {
                        candidato.setSku(generarSku(candidato.getDescripcion()));
                    }
                    return productoRepo.saveAndFlush(candidato);
                });
                // Actualizar el formulario sólo después de confirmar el commit.
                BeanUtils.copyProperties(guardado, producto);
                return;
            } catch (DataIntegrityViolationException e) {
                if (!generarAutomaticamente || intento >= MAX_INTENTOS_SKU
                        || !esSkuDuplicado(e, candidato.getSku())) {
                    throw e;
                }
            }
        }
    }

    private boolean esSkuDuplicado(DataIntegrityViolationException error, String sku) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) {
            if (causa instanceof SQLException sql && sql.getErrorCode() == 1062) {
                return sku != null && productoRepo.existsBySku(sku);
            }
        }
        return false;
    }

    public Producto updateProducto(Long id, Producto producto) {

        Producto existente = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: " + id));

        existente.setDescripcion(producto.getDescripcion());
        existente.setCantidad(producto.getCantidad());
        existente.setPrecioCompra(producto.getPrecioCompra());
        existente.setPrecioContado(producto.getPrecioContado());
        existente.setPrecioTarjeta(producto.getPrecioTarjeta());
        existente.setPrecioCuentaCorriente(producto.getPrecioCuentaCorriente());

        // ===== PROVEEDOR =====
        if (producto.getProveedor() != null
                && producto.getProveedor().getId() != null) {

            Proveedor proveedor = proveedorRepository
                    .findById(producto.getProveedor().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Proveedor no encontrado"));

            existente.setProveedor(proveedor);
        }

        presupuestoService.actualizarPrecioProductoEnPendientes(
                existente.getId(),
                existente.getPrecioContado(),
                existente.getPrecioTarjeta(),
                existente.getPrecioCuentaCorriente());

        return productoRepo.save(existente);

    }

    public void deleteProducto(Long id) {
        if (movimientoRepo.existsByProductoId(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar el producto porque tiene movimientos"
            );
        }
        productoRepo.deleteById(id);
    }


    public List<Producto> buscar(String q) {
        return productoRepo
                .findByDescripcionContainingIgnoreCaseOrSkuContainingIgnoreCase(
                        q, q
                );
    }
}
