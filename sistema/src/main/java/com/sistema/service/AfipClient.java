package com.sistema.service;

import com.sistema.model.AfipFacturaRequest;
import com.sistema.model.AfipFacturaResponse;
import com.sistema.model.TipoComprobante;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AfipClient {

    public Long obtenerUltimoNumero(Integer puntoVenta, TipoComprobante tipo) {

        // 🔴 Esto es simulado
        // Después acá va la llamada real a AFIP
        return 1000L;
    }

    public AfipFacturaResponse facturar(AfipFacturaRequest request) {

        // 🔴 Simulación
        // Después reemplazás por WSFEv1 real

        String caeSimulado = "12345678901234";

        return new AfipFacturaResponse(
                caeSimulado,
                LocalDate.now().plusDays(10)
        );
    }
}

