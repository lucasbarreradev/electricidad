package com.sistema.service;

import com.sistema.model.MovimientoInventario;
import com.sistema.model.Producto;
import com.sistema.repository.MovimientoInventarioRepository;
import com.sistema.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MovimientoInventarioService {

        private final MovimientoInventarioRepository movRepo;
        private final ProductoRepository productoRepo;

        public MovimientoInventarioService(
                MovimientoInventarioRepository movRepo,
                ProductoRepository productoRepo
        ) {
            this.movRepo = movRepo;
            this.productoRepo = productoRepo;
        }

    public MovimientoInventario registrarDevolucion(
            Long productoId,
            Integer cantidad,
            String nota
    ) {
        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Producto no encontrado"));

        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setTipo(MovimientoInventario.Tipo.ENTRADA);
        mov.setCantidad(cantidad);
        mov.setStockPrevio(producto.getCantidad());
        mov.setFechaMovimiento(LocalDateTime.now());

        // 🔁 Devolver stock
        producto.setCantidad(producto.getCantidad() + cantidad);

        mov.setStockPosterior(producto.getCantidad());

        return movRepo.save(mov);
    }

        @Transactional
        public MovimientoInventario registrarVenta(
                Long productoId,
                Integer cantidad,
                String nota
        ) {
            Producto producto = productoRepo.findById(productoId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Producto no encontrado"));

            if (cantidad <= 0) {
                throw new IllegalArgumentException("Cantidad inválida");
            }

            if (producto.getCantidad() < cantidad) {
                throw new IllegalStateException("Stock insuficiente. Disponible: " + producto.getCantidad()
                );
            }

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(producto);
            mov.setTipo(MovimientoInventario.Tipo.SALIDA);
            mov.setCantidad(cantidad);
            mov.setStockPrevio(producto.getCantidad());
            mov.setFechaMovimiento(LocalDateTime.now());

            // Actualizar stock
            producto.setCantidad(producto.getCantidad() - cantidad);

            mov.setStockPosterior(producto.getCantidad());

            return movRepo.save(mov);
        }


    }

