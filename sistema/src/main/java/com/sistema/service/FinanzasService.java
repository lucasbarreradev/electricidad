package com.sistema.service;

import com.sistema.model.Venta;
import com.sistema.repository.GastoRepository;
import com.sistema.repository.VentaItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FinanzasService {

    private final VentaItemRepository ventaItemRepository;
    private final GastoRepository gastoRepository;

    public FinanzasService(VentaItemRepository ventaItemRepository,
                           GastoRepository gastoRepository)
    {
        this.ventaItemRepository = ventaItemRepository;
        this.gastoRepository = gastoRepository;
    }

    public BigDecimal gananciaEntreFechas(
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        BigDecimal margen = ventaItemRepository
                .margenEntreFechas(inicio, fin, Venta.Estado.ANULADA);

        BigDecimal gastos = gastoRepository
                .gastosEntreFechas(inicio, fin);

        if (margen == null) margen = BigDecimal.ZERO;
        if (gastos == null) gastos = BigDecimal.ZERO;

        return margen.subtract(gastos);
    }


}



