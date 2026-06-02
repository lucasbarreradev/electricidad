package com.sistema.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "presupuesto")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@ToString(exclude = {"detalles", "cliente"})
public class Presupuesto {

    public enum Estado {
        PENDIENTE,
        APROBADO,
        RECHAZADO,
        VENDIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 20)
    private String codigo;

    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name="cliente_id", nullable = true)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    private EstadoPresupuesto estado;

    @OneToMany(mappedBy = "presupuesto",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<DetallePresupuesto> detalles = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "fecha_validez")
    private LocalDate fechaValidez;

    // Forma de pago
    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    public enum Moneda {
        ARS,
        USD
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda", nullable = false, length = 3)
    private Moneda moneda = Moneda.ARS;

    @Column(name = "tipo_cambio", precision = 10, scale = 2)
    private BigDecimal tipoCambio;

    public void agregarDetalle(DetallePresupuesto detalle) {
        detalle.setPresupuesto(this);
        detalles.add(detalle);
    }

    public void calcularTotal() {
        this.total = detalles.stream()
                .map(DetallePresupuesto::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getFechaFormateada() {
        return fecha != null
                ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
    }

}
