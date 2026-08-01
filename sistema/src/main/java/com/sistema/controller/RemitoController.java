package com.sistema.controller;

import com.sistema.model.Cliente;
import com.sistema.model.FormaPago;
import com.sistema.model.Remito;
import com.sistema.model.Venta;
import com.sistema.repository.ClienteRepository;
import com.sistema.repository.VentaRepository;
import com.sistema.service.RemitoImpresionService;
import com.sistema.service.RemitoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/remitos")
public class RemitoController {

    private final RemitoService remitoService;
    private final ClienteRepository clienteRepo;
    private final RemitoImpresionService remitoImpresionService;
    private final VentaRepository ventaRepo;

    public RemitoController(RemitoService remitoService,
                            ClienteRepository clienteRepo,
                            RemitoImpresionService remitoImpresionService,
                            VentaRepository ventaRepo) {
        this.remitoService = remitoService;
        this.clienteRepo = clienteRepo;
        this.remitoImpresionService = remitoImpresionService;
        this.ventaRepo = ventaRepo;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String estado,
                         Model model) {
        List<Remito> remitos;

        if (estado != null && !estado.isBlank()) {
            try {
                remitos = remitoService.listarPorEstado(
                        Remito.Estado.valueOf(estado));
            } catch (IllegalArgumentException e) {
                remitos = remitoService.listarTodos();
            }
        } else {
            remitos = remitoService.listarTodos();
        }

        model.addAttribute("remitos", remitos);
        model.addAttribute("estadoFiltro", estado);
        return "remito/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo() {
        return "remito/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam("productoIds") List<Long> productoIds,
            @RequestParam("cantidades") List<Integer> cantidades,
            @RequestParam("precios") List<BigDecimal> precios,
            @RequestParam("descuentos") List<BigDecimal> descuentos,
            @RequestParam FormaPago formaPagoPrecio,
            @RequestParam(required = false) String direccionEntrega,
            @RequestParam(required = false) String observaciones,
            RedirectAttributes ra) {

        try {
            Cliente cliente = null;
            if (clienteId != null) {
                cliente = clienteRepo.findById(clienteId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Cliente no encontrado"));
            }

            Remito remito = remitoService.crearCuentaCorriente(
                    cliente,
                    productoIds,
                    cantidades,
                    precios,
                    descuentos,
                    formaPagoPrecio,
                    direccionEntrega,
                    observaciones);

            ra.addFlashAttribute(
                    "mensaje",
                    "Remito creado y stock descontado correctamente");
            return "redirect:/remitos/detalle/" + remito.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/remitos/nuevo";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("remito", remitoService.buscarPorId(id));
        return "remito/detalle";
    }

    @PostMapping("/entregar/{id}")
    public String marcarEntregado(@PathVariable Long id,
                                  RedirectAttributes ra) {
        try {
            remitoService.marcarComoEntregado(id);
            ra.addFlashAttribute(
                    "mensaje", "Remito entregado y stock descontado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/remitos/detalle/" + id;
    }

    @GetMapping("/convertir/{id}")
    public String formConvertir(@PathVariable Long id, Model model) {
        model.addAttribute("remito", remitoService.buscarPorId(id));
        return "remito/convertir";
    }

    @PostMapping("/convertir/{id}")
    public String convertirAVenta(@PathVariable Long id,
                                  @RequestParam FormaPago formaPago,
                                  RedirectAttributes ra) {
        try {
            Venta venta = remitoService.convertirAVenta(id, formaPago);
            ra.addFlashAttribute(
                    "mensaje", "Pago registrado y venta creada correctamente");
            return "redirect:/ventas/detalle/" + venta.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/remitos/detalle/" + id;
        }
    }

    @PostMapping("/anular/{id}")
    public String anular(@PathVariable Long id, RedirectAttributes ra) {
        try {
            remitoService.anular(id);
            ra.addFlashAttribute(
                    "mensaje", "Remito anulado y stock devuelto");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/remitos";
    }

    @GetMapping("/{id}/pdf")
    public void imprimirPdf(@PathVariable Long id,
                            HttpServletResponse response) {
        try {
            Remito remito = remitoService.buscarPorId(id);
            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=Remito_"
                            + remito.getCodigo() + ".pdf");

            OutputStream out = response.getOutputStream();
            remitoImpresionService.generarRemitoPdf(remito, out);
            out.flush();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo generar el PDF del remito", e);
        }
    }

    @GetMapping("/venta/{ventaId}/pdf")
    public void generarDesdeVenta(@PathVariable Long ventaId,
                                  HttpServletResponse response) {
        try {
            Remito remito = remitoService.buscarPorVentaId(ventaId);

            if (remito == null) {
                Venta venta = ventaRepo.findById(ventaId)
                        .orElseThrow(() ->
                                new RuntimeException("Venta no encontrada"));
                remito = remitoService.crearDesdeVenta(venta);
            }

            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=Remito_"
                            + remito.getCodigo() + ".pdf");

            OutputStream out = response.getOutputStream();
            remitoImpresionService.generarRemitoPdf(remito, out);
            out.flush();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo generar el PDF del remito", e);
        }
    }
}
