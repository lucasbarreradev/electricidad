package com.sistema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String sku;
    @ManyToOne
    @JoinColumn(name="proveedor_id")
    @JsonIgnore
    private Proveedor proveedor;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioCompra;
    private BigDecimal precioContado;
    private BigDecimal precioTarjeta;
    private BigDecimal precioCuentaCorriente;
    @Enumerated(EnumType.STRING)
    private TipoIva tipoIva;

    public BigDecimal getPrecioSegunFormaPago(FormaPago formaPago) {

        if (formaPago == null) {
            throw new IllegalArgumentException("Forma de pago no puede ser null");
        }

        switch (formaPago) {
            case CONTADO:
                return this.precioContado;

            case TARJETA:
                return this.precioTarjeta;

            case CUENTA_CORRIENTE:
                return this.precioCuentaCorriente;

            default:
                throw new IllegalArgumentException("Forma de pago inválida");
        }
    }


}
