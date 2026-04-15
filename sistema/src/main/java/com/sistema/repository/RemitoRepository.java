package com.sistema.repository;

import com.sistema.model.Remito;
import com.sistema.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RemitoRepository extends JpaRepository<Remito, Long> {

    Optional<Remito> findByCodigo(String codigo);

    List<Remito> findByVentaId(Long ventaId);

    List<Remito> findByEstadoOrderByFechaEmisionDesc(Remito.Estado estado);

    List<Remito> findByClienteIdOrderByFechaEmisionDesc(Long clienteId);

    List<Remito> findAllByOrderByFechaEmisionDesc();

    List<Remito> findByEstadoAndTipoOrderByFechaEmisionDesc(
            Remito.Estado estado,
            Remito.Tipo tipo
    );
}
