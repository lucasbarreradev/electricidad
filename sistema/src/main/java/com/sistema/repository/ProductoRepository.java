package com.sistema.repository;

import com.sistema.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findAllByOrderByDescripcionAsc();
    List<Producto> findByDescripcionContainingIgnoreCaseOrSkuContainingIgnoreCase(
            String descripcion,
            String sku
    );

    @Query("""
SELECT p.sku
FROM Producto p 
WHERE p.sku LIKE CONCAT(:prefijo, '-%')
""")
    List<String> findSkusByPrefix(@Param("prefijo") String prefijo);

    boolean existsBySku(String sku);

    boolean existsByProveedorId(Long proveedorId);
}
