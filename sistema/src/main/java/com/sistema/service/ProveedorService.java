package com.sistema.service;

import com.sistema.model.Proveedor;
import com.sistema.repository.ProductoRepository;
import com.sistema.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepo;
    private final ProductoRepository productoRepo;

    public ProveedorService(ProveedorRepository proveedorRepo,
                            ProductoRepository productoRepo) {
        this.proveedorRepo = proveedorRepo;
        this.productoRepo = productoRepo;
    }

    public List<Proveedor> getProveedores() {
        return proveedorRepo.findAllByOrderByNombreRazonSocialAsc();
    }

    public Optional<Proveedor> getProveedorById(Long id) {
        return proveedorRepo.findById(id);
    }

    public Proveedor saveProveedor(Proveedor proveedor) {
        return proveedorRepo.save(proveedor);
    }

    public Proveedor updateProveedor(Long id, Proveedor proveedor) {
        Proveedor existente = proveedorRepo.findById(id).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));
        existente.setNombreRazonSocial(proveedor.getNombreRazonSocial());
        existente.setCuit(proveedor.getCuit());
        existente.setTelefono(proveedor.getTelefono());
        existente.setEmail(proveedor.getEmail());
        existente.setCondicionIva(proveedor.getCondicionIva());
        existente.setDireccion(proveedor.getDireccion());
        // setear SOLO lo que se permite modificar
        return proveedorRepo.save(existente);
    }

    public void deleteProveedor(Long id) {
        if (productoRepo.existsByProveedorId(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar el proveedor porque tiene movimientos"
            );
        }
        proveedorRepo.deleteById(id);
    }
}
