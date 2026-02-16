package com.sistema.repository;

import com.sistema.model.EstadoPresupuesto;
import com.sistema.model.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {

    Optional<Presupuesto> findByCodigo(String codigo);

    List<Presupuesto> findAllByOrderByFechaDesc();

    List<Presupuesto> findByEstadoOrderByFechaDesc(EstadoPresupuesto estado);

    List<Presupuesto> findByClienteIdOrderByFechaDesc(Long clienteId);


}
