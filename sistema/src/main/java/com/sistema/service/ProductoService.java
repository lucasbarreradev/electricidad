package com.sistema.service;

import com.sistema.model.Producto;
import com.sistema.model.Proveedor;
import com.sistema.repository.MovimientoInventarioRepository;
import com.sistema.repository.ProductoRepository;
import com.sistema.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepo;
    private final ProveedorRepository proveedorRepository;
    private final MovimientoInventarioRepository movimientoRepo;

    public ProductoService(ProductoRepository productoRepo,
                            ProveedorRepository proveedorRepository,
                           MovimientoInventarioRepository movimientoRepo) {
        this.productoRepo = productoRepo;
        this.proveedorRepository = proveedorRepository;
        this.movimientoRepo = movimientoRepo;
    }

    public List<Producto> getProductos() {
        return productoRepo.findAllByOrderByDescripcionAsc();
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepo.findById(id);
    }

    public String generarSku(String descripcion) {

        String base = descripcion
                .toUpperCase()
                .replaceAll("[^A-Z]", "");

        String prefijo = base.substring(0, Math.min(4, base.length()));

        long count = productoRepo.countBySkuPrefix(prefijo) + 1;

        return prefijo + "-" + String.format("%03d", count);
    }



    public void saveProducto(Producto producto) {

        if (producto.getProveedor() != null
                && producto.getProveedor().getId() != null) {

            Proveedor proveedor = proveedorRepository
                    .findById(producto.getProveedor().getId())
                    .orElse(null);

            producto.setProveedor(proveedor);
        }

        if (producto.getSku() == null || producto.getSku().isEmpty()) {
            producto.setSku(generarSku(producto.getDescripcion()));
        }

        productoRepo.save(producto);
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
