package com.edu.service.impl;

import com.edu.entity.Cliente;
import com.edu.entity.EstadoCliente;
import com.edu.exception.BusinessException;
import com.edu.repository.ClienteRepository;
import com.edu.service.ClienteService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // === CRUD ===
    @Override
    public Cliente crearCliente(Cliente cliente) {
        if (existeCorreo(cliente.getCorreo())) {
            throw new BusinessException("El correo ya está registrado: " + cliente.getCorreo());
        }
        if (existeTelefono(cliente.getTelefono())) {
            throw new BusinessException("El teléfono ya está registrado: " + cliente.getTelefono());
        }

        cliente.setEstado(EstadoCliente.ACTIVO); // por defecto
        cliente.setFechaRegistro(LocalDateTime.now());

        return clienteRepository.save(cliente);
    }

    @Override
    public Page<Cliente> listarClientes(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public Page<Cliente> buscarClientes(String keyword, Pageable pageable) {
        return clienteRepository.buscarClientes(keyword, pageable);

    }


    @Override
    public Cliente actualizarCliente(Long id, Cliente cliente) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado con id: " + id));

        // Validaciones si se cambia correo o teléfono
        if (!existente.getCorreo().equals(cliente.getCorreo()) && existeCorreo(cliente.getCorreo())) {
            throw new BusinessException("El correo ya está registrado: " + cliente.getCorreo());
        }

        if (!existente.getTelefono().equals(cliente.getTelefono()) && existeTelefono(cliente.getTelefono())) {
            throw new BusinessException("El teléfono ya está registrado: " + cliente.getTelefono());
        }

        existente.setNombre(cliente.getNombre());
        existente.setDireccion(cliente.getDireccion());
        existente.setCorreo(cliente.getCorreo());
        existente.setTelefono(cliente.getTelefono());
        existente.setEstado(cliente.getEstado());

        return clienteRepository.save(existente);
    }

    @Override
    public void eliminarCliente(Long id) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado con id: " + id));

        // Eliminación lógica (desactivar cliente)
        existente.setEstado(EstadoCliente.INACTIVO);
        clienteRepository.save(existente);
    }

    @Override
    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Integer SumaListaClientes() {
       return clienteRepository.findAll().size();
    }

    // === Búsquedas ===
    @Override
    public Optional<Cliente> obtenerPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    @Override
    public Optional<Cliente> obtenerPorNombre(String nombre) {
        return clienteRepository.findByNombre(nombre);
    }

    @Override
    public void cambiarEstado(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado con id: " + id));

        // defensa por si estado es null (por si acaso)
        if (cliente.getEstado() == null) {
            cliente.setEstado(EstadoCliente.ACTIVO);
        }

        // comparar enums correctamente (==)
        if (cliente.getEstado() == EstadoCliente.ACTIVO) {
            cliente.setEstado(EstadoCliente.INACTIVO);
        } else {
            cliente.setEstado(EstadoCliente.ACTIVO);
        }

        clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> buscarPorNombre(String nombreParcial) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombreParcial);
    }

    @Override
    public List<Cliente> obtenerPorEstado(EstadoCliente estado) {
        return clienteRepository.findByEstado(estado);
    }

    @Override
    public Optional<Cliente> obtenerPorTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono);
    }

    // === Validaciones ===
    @Override
    public boolean existeCorreo(String correo) {
        return clienteRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existeTelefono(String telefono) {
        return clienteRepository.existsByTelefono(telefono);
    }

    // === Reportes ===
    @Override
    public List<Cliente> listarClientesRegistradosDespuesDe(LocalDateTime fecha) {
        return clienteRepository.findByFechaRegistroAfter(fecha);
    }

    @Override
    public List<Cliente> listarClientesRegistradosEntre(LocalDateTime inicio, LocalDateTime fin) {
        if (clienteRepository.findByFechaRegistroBetween(inicio, fin) == null) {
            throw new BusinessException("No hay registros registrados");
        }
        return clienteRepository.findByFechaRegistroBetween(inicio, fin);
    }
}
