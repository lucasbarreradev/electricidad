package com.sistema.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AfipFacturaRequest {

    private Integer puntoVenta;
    private TipoComprobante tipoComprobante;
    private Long numeroComprobante;

    private BigDecimal importeNeto;
    private BigDecimal importeIva;
    private BigDecimal importeTotal;

    private Cliente cliente;
}

