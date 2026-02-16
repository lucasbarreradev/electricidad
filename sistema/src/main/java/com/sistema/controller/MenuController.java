package com.sistema.controller;


import com.sistema.model.Venta;
import com.sistema.repository.VentaItemRepository;
import com.sistema.repository.VentaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class MenuController {

    private final VentaItemRepository ventaItemRepository;
    private final VentaRepository ventaRepository;

    public MenuController(VentaItemRepository ventaItemRepository,
                          VentaRepository ventaRepository) {
        this.ventaItemRepository = ventaItemRepository;
        this.ventaRepository = ventaRepository;
    }


    @GetMapping("/")
    public String index(Model model) {

        LocalDate hoy = LocalDate.now();
        Venta.Estado estadoExcluido = Venta.Estado.ANULADA;

        // =========================
        // 📅 DÍA
        // =========================
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(23, 59, 59, 999_999_999);

        Long ventasDia = ventaRepository.contarVentasDelDia(
                inicioDia,
                finDia,
                estadoExcluido
        );

        BigDecimal gananciaDia = ventaItemRepository.gananciaDelDia(
                inicioDia,
                finDia,
                estadoExcluido
        );

        BigDecimal caja = ventaItemRepository.cajaDelDia(
                inicioDia,
                finDia,
                estadoExcluido
        );

        // =========================
        // 📆 MES
        // =========================
        LocalDate primerDiaMes = hoy.withDayOfMonth(1);
        LocalDate ultimoDiaMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        LocalDateTime inicioMes = primerDiaMes.atStartOfDay();
        LocalDateTime finMes = ultimoDiaMes.atTime(23, 59, 59, 999_999_999);

        Long ventasMes = ventaRepository.contarVentasDelMes(
                inicioMes,
                finMes,
                estadoExcluido
        );

        BigDecimal gananciaMes = ventaItemRepository.gananciaEntreFechas(
                inicioMes,
                finMes,
                estadoExcluido
        );

        // =========================
        // 🛡️ NULL SAFE
        // =========================
        model.addAttribute("ventasDia", ventasDia != null ? ventasDia : 0L);
        model.addAttribute("ventasMes", ventasMes != null ? ventasMes : 0L);

        model.addAttribute("gananciaDia",
                gananciaDia != null ? gananciaDia : BigDecimal.ZERO);

        model.addAttribute("gananciaMes",
                gananciaMes != null ? gananciaMes : BigDecimal.ZERO);

        model.addAttribute("caja",
                caja != null ? caja : BigDecimal.ZERO);

        return "index";
    }



    @GetMapping("/home")
    public String homeAlias() {
        return "index";
    }
}

