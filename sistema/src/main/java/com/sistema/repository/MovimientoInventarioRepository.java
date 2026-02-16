package com.sistema.repository;

import com.sistema.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    boolean existsByProductoId(Long productoId);
}
