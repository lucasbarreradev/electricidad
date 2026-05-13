package com.sistema.repository;

import com.sistema.model.EstadoPresupuesto;
import com.sistema.model.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {

    Optional<Presupuesto> findByCodigo(String codigo);

    List<Presupuesto> findAllByOrderByFechaDesc();

    List<Presupuesto> findByEstadoOrderByFechaDesc(EstadoPresupuesto estado);

    List<Presupuesto> findByClienteIdOrderByFechaDesc(Long clienteId);

    @Query("SELECT p FROM Presupuesto p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto WHERE p.id = :id")
    Optional<Presupuesto> findByIdConDetalles(@Param("id") Long id);

}
