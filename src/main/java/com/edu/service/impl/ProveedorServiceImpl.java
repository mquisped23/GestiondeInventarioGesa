package com.edu.service.impl;

import com.edu.entity.EstadoProveedor;
import com.edu.entity.Proveedor;
import com.edu.exception.BusinessException;
import com.edu.repository.ProveedorRepository;
import com.edu.service.ProveedorService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    // === CRUD ===
    @Override
    public Proveedor crearProveedor(Proveedor proveedor) {
        if (existeRuc(proveedor.getRuc())) {
            throw new BusinessException("El RUC ya está registrado: " + proveedor.getRuc());
        }
        if (proveedor.getRuc().length() < 11){
            throw new BusinessException("El RUC debe tener 11 caracteres: Ejemp: 12345678911" );
        }
        if (existeCorreo(proveedor.getCorreo())) {
            throw new BusinessException("El correo ya está registrado: " + proveedor.getCorreo());
        }
        if (existeTelefono(proveedor.getTelefono())) {
            throw new BusinessException("El teléfono ya está registrado: " + proveedor.getTelefono());
        }

        proveedor.setEstado(EstadoProveedor.ACTIVO); // por defecto
        proveedor.setFechaRegistro(LocalDateTime.now());

        return proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor actualizarProveedor(Long id, Proveedor proveedor) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Proveedor no encontrado con id: " + id));

        // Validaciones si se cambian campos únicos
        if (existente.getRuc().length()<11) {
            throw new BusinessException("El RUC debe tener 11 caracteres: Ejemp: 12345678911" );
        }

        if (!existente.getRuc().equals(proveedor.getRuc()) && existeRuc(proveedor.getRuc())) {
            throw new BusinessException("El RUC ya está registrado: " + proveedor.getRuc());
        }

        if (!existente.getCorreo().equals(proveedor.getCorreo()) && existeCorreo(proveedor.getCorreo())) {
            throw new BusinessException("El correo ya está registrado: " + proveedor.getCorreo());
        }

        if (!existente.getTelefono().equals(proveedor.getTelefono()) && existeTelefono(proveedor.getTelefono())) {
            throw new BusinessException("El teléfono ya está registrado: " + proveedor.getTelefono());
        }

        // Actualizar campos
        existente.setNombre(proveedor.getNombre());
        existente.setDireccion(proveedor.getDireccion());
        existente.setCorreo(proveedor.getCorreo());
        existente.setTelefono(proveedor.getTelefono());
        existente.setRuc(proveedor.getRuc());
        existente.setEstado(proveedor.getEstado());

        return proveedorRepository.save(existente);
    }

    @Override
    public void eliminarProveedor(Long id) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Proveedor no encontrado con id: " + id));

        // Eliminación lógica (cambiar estado)
        existente.setEstado(EstadoProveedor.INACTIVO);
        proveedorRepository.save(existente);
    }

    @Override
    public Optional<Proveedor> obtenerProveedorPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    @Override
    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }

    @Override
    public Page<Proveedor> listarProveedores(Pageable pageable) {
        return proveedorRepository.findAll(pageable);
    }

    @Override
    public Page<Proveedor> buscarProveedores(String keyword, Pageable pageable) {
        return proveedorRepository.buscarProveedores(keyword, pageable);
    }

    // === Búsquedas ===
    @Override
    public Optional<Proveedor> obtenerPorRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc);
    }

    @Override
    public Optional<Proveedor> obtenerPorCorreo(String correo) {
        return proveedorRepository.findByCorreo(correo);
    }

    @Override
    public Optional<Proveedor> obtenerPorNombre(String nombre) {
        return proveedorRepository.findByNombre(nombre);
    }

    @Override
    public List<Proveedor> buscarPorNombre(String nombreParcial) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombreParcial);
    }

    @Override
    public List<Proveedor> obtenerPorEstado(EstadoProveedor estado) {
        return proveedorRepository.findByEstado(estado);
    }

    @Override
    public Optional<Proveedor> obtenerPorTelefono(String telefono) {
        return proveedorRepository.findByTelefono(telefono);
    }

    // === Validaciones ===
    @Override
    public boolean existeRuc(String ruc) {
        return proveedorRepository.existsByRuc(ruc);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return proveedorRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existeTelefono(String telefono) {
        return proveedorRepository.existsByTelefono(telefono);
    }

    // === Reportes ===
    @Override
    public List<Proveedor> listarProveedoresRegistradosDespuesDe(LocalDateTime fecha) {
        return proveedorRepository.findByFechaRegistroAfter(fecha);
    }

    @Override
    public List<Proveedor> listarProveedoresRegistradosEntre(LocalDateTime inicio, LocalDateTime fin) {
        return proveedorRepository.findByFechaRegistroBetween(inicio, fin);
    }

    @Override
    public void cambiarEstado(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Proveedor no encontrado con id: " + id));

        if (proveedor.getEstado() == null) {
            proveedor.setEstado(EstadoProveedor.ACTIVO);
        }

        if (proveedor.getEstado() == EstadoProveedor.ACTIVO) {
            proveedor.setEstado(EstadoProveedor.INACTIVO);
        } else {
            proveedor.setEstado(EstadoProveedor.ACTIVO);
        }

        proveedorRepository.save(proveedor);
    }
}