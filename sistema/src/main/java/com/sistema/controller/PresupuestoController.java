package com.sistema.controller;

import com.sistema.model.*;
import com.sistema.repository.*;
import com.sistema.service.PresupuestoPdfService;
import com.sistema.service.PresupuestoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;
    private final ProductoRepository productoRepo;
    private final ClienteRepository clienteRepo;
    private final PresupuestoRepository presupuestoRepo;
    private final PresupuestoPdfService presupuestoPdfService;

    public PresupuestoController(PresupuestoService presupuestoService,
                                 ProductoRepository productoRepo,
                                 ClienteRepository clienteRepo,
                                 PresupuestoRepository presupuestoRepo,
                                 PresupuestoPdfService presupuestoPdfService) {
        this.presupuestoService = presupuestoService;
        this.productoRepo = productoRepo;
        this.clienteRepo = clienteRepo;
        this.presupuestoRepo = presupuestoRepo;
        this.presupuestoPdfService = presupuestoPdfService;
    }

    // ==========================================
    // LISTAR PRESUPUESTOS
    // ==========================================
    @GetMapping
    public String listar(@RequestParam(required = false) String estado,
                         Model model) {

        List<Presupuesto> presupuestos;

        if (estado != null && !estado.isEmpty()) {
            EstadoPresupuesto estadoEnum = EstadoPresupuesto.valueOf(estado);
            presupuestos = presupuestoService.buscarPorEstado(estadoEnum);
        } else {
            presupuestos = presupuestoService.buscarTodos();
        }

        model.addAttribute("presupuestos", presupuestos);
        model.addAttribute("estados", EstadoPresupuesto.values());
        model.addAttribute("filtroEstado", estado);

        return "presupuesto/listar";
    }

    // ==========================================
    // FORM NUEVO PRESUPUESTO
    // ==========================================
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("productos", productoRepo.findAll());
        model.addAttribute("clientes", clienteRepo.findAll());

        return "presupuesto/form";
    }

    // ==========================================
    // CREAR PRESUPUESTO
    // ==========================================
    @PostMapping("/guardar")
    public void guardar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam FormaPago formaPago,
            @RequestParam List<Long> productoIds,
            @RequestParam List<Integer> cantidades,
            @RequestParam(required = false) List<BigDecimal> descuentos,
            HttpServletResponse response
    ) {

        try {
            Presupuesto presupuesto = presupuestoService.crear(
                    clienteId,
                    formaPago,
                    productoIds,
                    cantidades,
                    descuentos
            );

            response.reset(); // 🔴 CLAVE
            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"presupuesto_" + presupuesto.getCodigo() + ".pdf\""
            );

            try (OutputStream out = response.getOutputStream()) {
                presupuestoPdfService.generarPdf(presupuesto, out);
                out.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al generar presupuesto", e);
        }
    }





    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {

        Presupuesto p = presupuestoService.buscarPorId(id);

        model.addAttribute("presupuesto", p);
        model.addAttribute("fechaPresupuestoFmt", p.getFechaFormateada());

        return "presupuesto/detallePresupuesto";
    }

    @GetMapping("/detalle/{id}")
    public String verPresupuesto(@PathVariable Long id, Model model) {

        Presupuesto presupuesto = presupuestoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Presupuesto no encontrada"));

        // ✅ Esto devuelve neto, IVA, total y el map de ivas
        TotalesConIva totales = presupuestoService.calcularTotalesConIvaMap(presupuesto);

        model.addAttribute("presupuesto", presupuesto);
        model.addAttribute("totales", totales);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        model.addAttribute("fechaPresupuestoFmt",
                presupuesto.getFecha().format(formatter));

        return "presupuesto/detallePresupuesto";
    }

    // ==========================================
    // BUSCAR POR CÓDIGO
    // ==========================================
    @GetMapping("/buscar")
    public String buscarPorCodigo(@RequestParam String codigo,
                                  RedirectAttributes ra) {
        try {
            Presupuesto presupuesto = presupuestoService.buscarPorCodigo(codigo);
            return "redirect:/presupuestos/" + presupuesto.getId();

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/presupuestos";
        }
    }

    // ==========================================
    // FORM EDITAR (solo PENDIENTE)
    // ==========================================
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         Model model,
                         RedirectAttributes ra) {

        try {
            Presupuesto presupuesto = presupuestoService.buscarPorId(id);

            if (presupuesto.getEstado() != EstadoPresupuesto.PENDIENTE) {
                ra.addFlashAttribute("error",
                        "Solo se puede editar un presupuesto PENDIENTE");
                return "redirect:/presupuestos/" + id;
            }

            model.addAttribute("presupuesto", presupuesto);
            model.addAttribute("productos", productoRepo.findAll());
            model.addAttribute("clientes", clienteRepo.findAll());
            return "presupuesto/form";

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/presupuestos";
        }
    }

    // ==========================================
    // ACTUALIZAR PRESUPUESTO
    // ==========================================
    @PostMapping("/{id}/actualizar")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(required = false) Long clienteId,
            @RequestParam List<Long> productoIds,
            @RequestParam List<Integer> cantidades,
            @RequestParam(required = false) List<BigDecimal> descuentos,
            @RequestParam FormaPago formaPago,
            RedirectAttributes ra) {

        try {
            presupuestoService.actualizar(id, clienteId, productoIds, cantidades, descuentos, formaPago);
            ra.addFlashAttribute("mensaje", "Presupuesto actualizado exitosamente");
            return "redirect:/presupuestos/" + id;

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/presupuestos/" + id + "/editar";
        }
    }

    // ==========================================
    // APROBAR → CREAR VENTA
    // ==========================================
    @PostMapping("/aprobar")
    public String aprobar(@RequestParam Long id,
                          RedirectAttributes ra) {

        try {
            Venta venta = presupuestoService.aprobar(id);

            ra.addFlashAttribute("mensaje",
                    "Presupuesto aprobado. Venta generada: " + venta.getCodigo());

            return "redirect:/ventas/detalle/" + venta.getId();

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/presupuestos/" + id;
        }
    }




    // ==========================================
    // RECHAZAR
    // ==========================================
    @PostMapping("/rechazar")
    public String rechazar(@RequestParam Long id,
                           RedirectAttributes ra) {

        try {
            presupuestoService.rechazar(id);
            ra.addFlashAttribute("mensaje",
                    "Presupuesto rechazado correctamente");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/presupuestos/" + id;
    }



    // ==========================================
    // ELIMINAR (solo PENDIENTE)
    // ==========================================

    @GetMapping("/{id}/pdf")
    public void generarPdf(
            @PathVariable Long id,
            HttpServletResponse response
    ) throws Exception {

        Presupuesto presupuesto = presupuestoService.buscarPorId(id);

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=presupuesto_" + presupuesto.getCodigo() + ".pdf"
        );

        presupuestoPdfService.generarPdf(
                presupuesto,
                response.getOutputStream()
        );
    }


}
