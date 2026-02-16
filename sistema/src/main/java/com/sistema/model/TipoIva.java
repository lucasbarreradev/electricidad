package com.sistema.model;

import java.math.BigDecimal;

public enum TipoIva {

    IVA_21(new BigDecimal("21.00"), 5, "IVA 21%"),
    IVA_10_5(new BigDecimal("10.50"), 4, "IVA 10.5%"),
    IVA_27(new BigDecimal("27.00"), 6, "IVA 27%"),
    EXENTO(BigDecimal.ZERO, 3, "Exento");

    private final BigDecimal porcentaje;
    private final Integer codigoAfip;
    private final String descripcion;

    TipoIva(BigDecimal porcentaje, Integer codigoAfip, String descripcion) {
        this.porcentaje = porcentaje;
        this.codigoAfip = codigoAfip;
        this.descripcion = descripcion;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public Integer getCodigoAfip() {
        return codigoAfip;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

