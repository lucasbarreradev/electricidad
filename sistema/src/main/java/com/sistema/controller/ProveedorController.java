package com.sistema.controller;

import com.sistema.model.Proveedor;
import com.sistema.service.ProveedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
    @RequestMapping("/proveedores")
    public class ProveedorController {

        private final ProveedorService proveedorService;

        public ProveedorController(ProveedorService proveedorService) {
            this.proveedorService = proveedorService;
        }

        // ==========================================
        // LISTAR proveedores
        // ==========================================
        @GetMapping
        public String listar(
                @RequestParam(required = false) String origen,
                @RequestParam(required = false) Long productoId,
                Model model) {

            model.addAttribute("proveedores",
                    proveedorService.getProveedores());

            model.addAttribute("origen", origen);
            model.addAttribute("productoId", productoId);
            return "proveedor/listar";
        }


    // ==========================================
        // FORM NUEVO proveedor
        // ==========================================
        @GetMapping("/nuevo")
        public String nuevo(Model model) {
            model.addAttribute("proveedor", new Proveedor());
            return "proveedor/form";
        }

    // ==========================================
        // GUARDAR proveedor (nuevo)
        // ==========================================
        @PostMapping("/guardar")
        public String guardar(@ModelAttribute Proveedor proveedor,
                              RedirectAttributes ra) {

            proveedorService.saveProveedor(proveedor);
            ra.addFlashAttribute("mensaje",
                    "Proveedor creado correctamente");

            return "redirect:/proveedores";
        }

        // ==========================================
        // FORM EDITAR proveedor
        // ==========================================
        @GetMapping("/editar/{id}")
        public String editar(@PathVariable Long id,
                             Model model,
                             RedirectAttributes ra) {

            Proveedor proveedor = proveedorService
                    .getProveedorById(id)
                    .orElse(null);

            if (proveedor == null) {
                ra.addFlashAttribute("error",
                        "Proveedor no encontrado");
                return "redirect:/proveedores";
            }

            model.addAttribute("proveedor", proveedor);
            return "proveedor/form";
        }

        // ==========================================
        // ACTUALIZAR proveedor
        // ==========================================
        @PostMapping("/actualizar/{id}")
        public String actualizar(@PathVariable Long id,
                                 @ModelAttribute Proveedor proveedor,
                                 RedirectAttributes ra) {

            proveedorService.updateProveedor(id, proveedor);
            ra.addFlashAttribute("mensaje",
                    "Proveedor actualizado correctamente");

            return "redirect:/proveedores";
        }

        // ==========================================
        // ELIMINAR proveedor
        // ==========================================
        @PostMapping("/eliminar/{id}")
        public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
            try {
                proveedorService.deleteProveedor(id);
                ra.addFlashAttribute("mensaje", "Proveedor eliminado");
            } catch (IllegalStateException e) {
                ra.addFlashAttribute("error", e.getMessage());
            }
            return "redirect:/proveedores";
        }
    }



