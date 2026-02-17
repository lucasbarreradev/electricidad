package com.sistema.controller;

import com.sistema.model.Cliente;
import com.sistema.model.CondicionIva;
import com.sistema.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clientes")

public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ==========================================
    // LISTAR clientes
    // ==========================================
    @GetMapping
    public String listar(
            @RequestParam(required = false) String origen,
            Model model) {

        model.addAttribute("clientes",
                clienteService.getClientes());

        model.addAttribute("origen", origen); // 👈 clave
        return "cliente/listar";
    }

    // ==========================================
    // FORM NUEVO cliente
    // ==========================================
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("condicionesIva", CondicionIva.values());
        return "cliente/form";
    }

    // ==========================================
    // GUARDAR cliente (nuevo)
    // ==========================================
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente,
                          RedirectAttributes ra) {

        clienteService.saveCliente(cliente);
        ra.addFlashAttribute("mensaje",
                "Cliente creado correctamente");

        return "redirect:/clientes";
    }

    // ==========================================
    // FORM EDITAR cliente
    // ==========================================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model,
                         RedirectAttributes ra) {

        Cliente cliente = clienteService
                .getClienteById(id)
                .orElse(null);

        if (cliente == null) {
            ra.addFlashAttribute("error",
                    "Cliente no encontrado");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("condicionesIva", CondicionIva.values());
        return "cliente/form";
    }

    // ==========================================
    // ACTUALIZAR cliente
    // ==========================================
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Cliente cliente,
                             RedirectAttributes ra) {

        clienteService.updateCliente(id, cliente);
        ra.addFlashAttribute("mensaje",
                "Cliente actualizado correctamente");

        return "redirect:/clientes";
    }

    // ==========================================
    // ELIMINAR cliente
    // ==========================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes ra) {

        clienteService.deleteCliente(id);
        ra.addFlashAttribute("mensaje",
                "Cliente eliminado correctamente");

        return "redirect:/clientes";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Cliente> buscar(@RequestParam String q) {
        return clienteService.buscar(q);
    }

}

