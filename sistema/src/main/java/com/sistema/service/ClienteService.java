package com.sistema.service;

import com.sistema.model.Cliente;
import com.sistema.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;

    public ClienteService(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    public List<Cliente> getClientes() {
        return clienteRepo.findAllByOrderByNombreAsc();
    }

    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepo.findById(id);
    }

    public Cliente saveCliente(Cliente cliente) {
        return clienteRepo.save(cliente);
    }

    public Cliente updateCliente(Long id, Cliente cliente) {
        Cliente existente = clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cliente no encontrado con id: " + id));

        existente.setNombre(cliente.getNombre());
        existente.setApellido(cliente.getApellido());
        existente.setTelefono(cliente.getTelefono());
        existente.setDni(cliente.getDni());
        existente.setEmail(cliente.getEmail());
        existente.setDireccion(cliente.getDireccion());
        existente.setCondicionIva(cliente.getCondicionIva());
        // setear SOLO lo que se permite modificar

        return clienteRepo.save(existente);
    }

    public void deleteCliente(Long id) {
        if (!clienteRepo.existsById(id)) {
            throw new RuntimeException("Cliente no existe");
        }
        clienteRepo.deleteById(id);
    }
}

