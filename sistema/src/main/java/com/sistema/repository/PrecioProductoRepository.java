package com.sistema.repository;

import com.sistema.model.FormaPago;
import com.sistema.model.PrecioProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrecioProductoRepository
        extends JpaRepository<PrecioProducto, Long> {

    Optional<PrecioProducto> findByProductoIdAndFormaPago(
            Long productoId,
            FormaPago formaPago
    );
}

