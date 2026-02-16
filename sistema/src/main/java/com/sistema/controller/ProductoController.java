package com.sistema.controller;

import com.sistema.model.Producto;
import com.sistema.model.Proveedor;
import com.sistema.model.TipoIva;
import com.sistema.service.ProductoService;
import com.sistema.service.ProveedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService productoService;
    private final ProveedorService proveedorService;

    public ProductoController(ProductoService productoService,
                              ProveedorService proveedorService) {
        this.productoService = productoService;
        this.proveedorService = proveedorService;
    }

    // ==========================================
    // LISTAR productos
    // ==========================================
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos",
                productoService.getProductos());
        return "producto/listar";
    }

    // ==========================================
    // FORM NUEVO producto
    // ==========================================
    @GetMapping("/nuevo")
    public String nuevo(
            @RequestParam(required = false) Long proveedorId,
            Model model) {

        Producto producto = new Producto();

        if (proveedorId != null) {
            Proveedor proveedor = proveedorService
                    .getProveedorById(proveedorId)
                    .orElse(null);

            if (proveedor != null) {
                producto.setProveedor(proveedor);
            }
        }


        model.addAttribute("producto", producto);
        model.addAttribute("tiposIva", TipoIva.values());
        return "producto/form";
    }

    // ==========================================
    // GUARDAR producto (nuevo)
    // ==========================================
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("producto") Producto producto,
                          @RequestParam(required = false) Long proveedorId,
                          SessionStatus status) {

        if (proveedorId != null) {
            Proveedor proveedor = proveedorService
                    .getProveedorById(proveedorId)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

            producto.setProveedor(proveedor);
        }

        productoService.saveProducto(producto);
        status.setComplete();

        return "redirect:/productos";
    }


    // ==========================================
    // FORM EDITAR producto
    // ==========================================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model,
                         RedirectAttributes ra) {

        Producto producto = productoService
                .getProductoById(id)
                .orElse(null);

        if (producto == null) {
            ra.addFlashAttribute("error",
                    "Producto no encontrado");
            return "redirect:/productos";
        }

        model.addAttribute("producto", producto);
        model.addAttribute("tiposIva", TipoIva.values());
        return "producto/form";
    }

    // ==========================================
    // ACTUALIZAR productos
    // ==========================================
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Producto producto,
                             RedirectAttributes ra) {


        productoService.updateProducto(id, producto);
        ra.addFlashAttribute("mensaje",
                "Producto actualizado correctamente");

        return "redirect:/productos";
    }

    // ==========================================
    // ELIMINAR producto
    // ==========================================
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productoService.deleteProducto(id);
            ra.addFlashAttribute("mensaje", "Producto eliminado");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }



    @GetMapping("/buscar")
    @ResponseBody
    public List<Producto> buscar(@RequestParam String q) {
        return productoService.buscar(q);
    }

}
