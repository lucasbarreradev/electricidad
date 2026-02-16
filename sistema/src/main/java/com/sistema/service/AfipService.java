package com.sistema.service;

import com.sistema.model.*;
import com.sistema.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AfipService {

    private final VentaRepository ventaRepo;
    private final AfipClient afipClient;
    public AfipService(VentaRepository ventaRepo,
                       AfipClient afipClient) {
        this.ventaRepo = ventaRepo;
        this.afipClient = afipClient;
    }

    @Transactional
    public void facturarConAfip(Venta venta) {


        // 1️⃣ Determinar tipo comprobante según cliente

        if (venta.getCliente().getCondicionIva() == CondicionIva.RESPONSABLE_INSCRIPTO) {
            venta.setTipoComprobante(TipoComprobante.FACTURA_A);
        } else {
            venta.setTipoComprobante(TipoComprobante.FACTURA_B);
        }

        // 2️⃣ Calcular totales fiscales
        venta.calcularTotales();

        // 3️⃣ Obtener último número comprobante AFIP
        Long ultimoNumero = afipClient.obtenerUltimoNumero(
                venta.getPuntoVenta(),
                venta.getTipoComprobante()
        );

        Long nuevoNumero = ultimoNumero + 1;
        venta.setNumeroComprobante(nuevoNumero);

        // 4️⃣ Construir request para AFIP

        AfipFacturaRequest request = AfipFacturaRequest.builder()
                .puntoVenta(venta.getPuntoVenta())
                .tipoComprobante(venta.getTipoComprobante())
                .numeroComprobante(nuevoNumero)
                .importeNeto(venta.getTotalNeto())
                .importeIva(venta.getTotalIva())
                .importeTotal(venta.getTotal())
                .cliente(venta.getCliente())
                .build();

        // 5️⃣ Enviar a AFIP
        AfipFacturaResponse response = afipClient.facturar(request);

        // 6️⃣ Guardar datos devueltos por AFIP
        venta.setCae(response.getCae());
        venta.setFechaVencimientoCae(response.getFechaVencimiento());
        venta.setEstado(Venta.Estado.FACTURADA);

        ventaRepo.save(venta);
    }
}
