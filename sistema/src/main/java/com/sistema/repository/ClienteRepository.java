package com.sistema.repository;

import com.sistema.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findAllByOrderByNombreAsc();
    @Query("SELECT DISTINCT c FROM Cliente c WHERE " +
            "LOWER(CONCAT(c.nombre, ' ', c.apellido)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CONCAT(c.apellido, ' ', c.nombre)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "COALESCE(c.dni, '') LIKE CONCAT('%', :query, '%') " +
            "ORDER BY " +
            "CASE " +
            "  WHEN LOWER(CONCAT(c.nombre, ' ', c.apellido)) = LOWER(:query) THEN 1 " +
            "  WHEN LOWER(c.nombre) = LOWER(:query) THEN 2 " +
            "  WHEN LOWER(c.apellido) = LOWER(:query) THEN 3 " +
            "  ELSE 4 " +
            "END, c.apellido, c.nombre")
    List<Cliente> buscar(@Param("query") String query);
}
