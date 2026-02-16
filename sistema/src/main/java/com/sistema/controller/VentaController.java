package com.sistema.controller;

import com.sistema.model.*;
import com.sistema.repository.ClienteRepository;
import com.sistema.repository.ProductoRepository;
import com.sistema.repository.VentaItemRepository;
import com.sistema.repository.VentaRepository;
import com.sistema.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ProductoRepository productoRepo;
    private final ClienteRepository clienteRepo;
    private final VentaRepository ventaRepo;
    private final VentaItemRepository ventaItemRepo;

    public VentaController(VentaService ventaService,
                           ProductoRepository productoRepo,
                           ClienteRepository clienteRepo,
                           VentaRepository ventaRepo,
                           VentaItemRepository ventaItemRepo) {
        this.ventaService = ventaService;
        this.productoRepo = productoRepo;
        this.clienteRepo = clienteRepo;
        this.ventaRepo = ventaRepo;
        this.ventaItemRepo = ventaItemRepo;
    }

    // ==========================================
    // LISTAR VENTAS
    @GetMapping
    public String listarVentas(Model model) {
        model.addAttribute("ventas", ventaService.listarVentasNoAnuladas());
        return "venta/listar";
    }
    // ==========================================
    // ==========================================
    // FORM NUEVA VENTA
    // ==========================================
    @GetMapping("/nueva")
    public String nuevaVenta(
            @RequestParam(required = false) Long clienteId,
            Model model) {

        Venta venta = new Venta();

        if (clienteId != null) {
            Cliente cliente = clienteRepo.findById(clienteId).orElse(null);
            venta.setCliente(cliente);
        }

        model.addAttribute("venta", venta);
        model.addAttribute("productos", productoRepo.findAll());
        model.addAttribute("clientes", clienteRepo.findAll());
        return "venta/form";
    }



    // ==========================================
    // CREAR VENTA DIRECTA
    // ==========================================
    @PostMapping("/guardar")
    public String guardarVenta(
            @RequestParam(required = false) Long clienteId,
            @RequestParam FormaPago formaPago,
            @RequestParam(required = false) String nota,
            @RequestParam(required = false) List<Long> productoIds,
            @RequestParam(required = false) List<Integer> cantidades,
            @RequestParam(required = false) List<BigDecimal> descuentos,
            RedirectAttributes ra
    ) {




        try {

            if (productoIds == null || productoIds.isEmpty()) {
                throw new IllegalArgumentException("Debe agregar al menos un producto");
            }

            if (cantidades == null || cantidades.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar cantidades");
            }

            if (productoIds.size() != cantidades.size()) {
                throw new IllegalArgumentException("Datos inválidos de productos");
            }

            List<VentaItem> items = new ArrayList<>();

            for (int i = 0; i < productoIds.size(); i++) {

                Producto producto = productoRepo.findById(productoIds.get(i))
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

                VentaItem item = new VentaItem();
                item.setProducto(producto);
                item.setCantidad(cantidades.get(i));


                if (descuentos != null && descuentos.size() == productoIds.size()) {
                    item.setDescuentoPct(descuentos.get(i));
                } else {
                    item.setDescuentoPct(BigDecimal.ZERO);
                }


                items.add(item);
            }


            ventaService.crearVentaDirecta(
                    clienteId,
                    items,
                    formaPago,
                    nota
            );

            ra.addFlashAttribute("mensaje", "Venta realizada correctamente");
            return "redirect:/ventas";

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }


    // ==========================================
    // CREAR VENTA DESDE PRESUPUESTO
    // ==========================================
    @PostMapping("/desde-presupuesto/{id}")
    public String venderDesdePresupuesto(
            @PathVariable Long id,
            @RequestParam FormaPago formaPago,
            @RequestParam(required = false) String nota,
            RedirectAttributes ra
    ) {
        try {
            ventaService.crearDesdePresupuesto(id, formaPago);
            ra.addFlashAttribute("mensaje", "Venta creada desde presupuesto");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas";
    }

    // ==========================================
    // VER DETALLE DE VENTA
    // ==========================================

    @GetMapping("/detalle/{id}")
    public String verVenta(@PathVariable Long id, Model model) {

        Venta venta = ventaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        // ✅ Esto devuelve neto, IVA, total y el map de ivas
        TotalesConIva totales = ventaService.calcularTotalesConIvaMap(venta);

        model.addAttribute("venta", venta);
        model.addAttribute("totales", totales);  //
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        model.addAttribute("fechaVentaFmt",
                venta.getFechaVenta().format(formatter));

        return "venta/detalleVenta";
    }


    @PostMapping("/anular")
    public String anularVenta(
            @RequestParam Long id,
            RedirectAttributes ra
    ) {
        try {
            ventaService.anularVenta(id);
            ra.addFlashAttribute("mensaje", "Venta anulada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas";
    }







}
