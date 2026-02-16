package com.sistema.repository;

import com.sistema.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findAllByOrderByNombreRazonSocialAsc();
}
