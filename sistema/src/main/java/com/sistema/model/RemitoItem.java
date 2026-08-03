package com.sistema.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "remito_item")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RemitoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "remito_id", nullable = false)
    private Remito remito;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = true)
    private Producto producto;

    @Lob
    @Column(name = "descripcion", columnDefinition = "LONGTEXT")
    private String descripcion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(name = "descuento_pct", precision = 5, scale = 2)
    private BigDecimal descuentoPct = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    // ==========================================
    // MÉTODOS
    // ==========================================

    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            BigDecimal bruto = precioUnitario.multiply(new BigDecimal(cantidad));
            BigDecimal descuento = descuentoPct != null
                    ? descuentoPct
                    : BigDecimal.ZERO;
            BigDecimal montoDescuento = bruto
                    .multiply(descuento)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            subtotal = bruto.subtract(montoDescuento);
        } else {
            subtotal = BigDecimal.ZERO;
        }
    }

    @Transient
    public String getDescripcionMostrada() {
        if (descripcion != null && !descripcion.isBlank()) {
            return descripcion;
        }
        return producto != null ? producto.getDescripcion() : "";
    }
}
