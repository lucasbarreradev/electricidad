package com.sistema.model;

import java.math.BigDecimal;
import java.util.Map;

public class TotalesConIva {
    private BigDecimal totalNeto;
    private BigDecimal totalIva;
    private BigDecimal total;
    private Map<BigDecimal, BigDecimal> ivasMap;

    public TotalesConIva(BigDecimal totalNeto, BigDecimal totalIva, BigDecimal total, Map<BigDecimal, BigDecimal> ivasMap) {
        this.totalNeto = totalNeto;
        this.totalIva = totalIva;
        this.total = total;
        this.ivasMap = ivasMap;
    }

    // Getters
    public BigDecimal getTotalNeto() { return totalNeto; }
    public BigDecimal getTotalIva() { return totalIva; }
    public BigDecimal getTotal() { return total; }
    public Map<BigDecimal, BigDecimal> getIvasMap() { return ivasMap; }
}

