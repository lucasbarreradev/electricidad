package com.sistema.repository;

import com.sistema.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByEstado(Venta.Estado estado);

    // Más genérico
    List<Venta> findByEstadoNotOrderByFechaVentaDesc(Venta.Estado estado);

    // =========================
    // 📅 CONTAR VENTAS DEL DÍA
    // =========================
    @Query("""
        SELECT COUNT(v)
        FROM Venta v
        WHERE v.fechaVenta BETWEEN :inicioDia AND :finDia
          AND v.estado <> :estado
    """)
    Long contarVentasDelDia(
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia,
            @Param("estado") Venta.Estado estado
    );

    // =========================
    // 📆 CONTAR VENTAS DEL MES
    // =========================
    @Query("""
        SELECT COUNT(v)
        FROM Venta v
        WHERE v.fechaVenta BETWEEN :inicioMes AND :finMes
          AND v.estado <> :estado
    """)
    Long contarVentasDelMes(
            @Param("inicioMes") LocalDateTime inicioMes,
            @Param("finMes") LocalDateTime finMes,
            @Param("estado") Venta.Estado estado
    );
}

