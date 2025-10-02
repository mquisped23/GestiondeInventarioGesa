package com.edu.service;

import com.edu.entity.Cliente;
import com.edu.entity.EstadoCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClienteService {

    // === Operaciones CRUD ===
    Cliente crearCliente(Cliente cliente);
    Cliente actualizarCliente(Long id, Cliente cliente);
    void eliminarCliente(Long id);
    Optional<Cliente> obtenerClientePorId(Long id);
    List<Cliente> listarClientes();
    Integer SumaListaClientes();
    // === Operaciones de búsqueda ===
    Page<Cliente> listarClientes(Pageable pageable);
    public Page<Cliente> buscarClientes(String keyword, Pageable pageable);
    Optional<Cliente> obtenerPorCorreo(String correo);
    Optional<Cliente> obtenerPorNombre(String nombre);
    List<Cliente> buscarPorNombre(String nombreParcial);
    List<Cliente> obtenerPorEstado(EstadoCliente estado);
    Optional<Cliente> obtenerPorTelefono(String telefono);

    // 👇 este faltaba
    void cambiarEstado(Long id);

    // === Validaciones ===
    boolean existeCorreo(String correo);
    boolean existeTelefono(String telefono);

    // === Reportes ===
    List<Cliente> listarClientesRegistradosDespuesDe(LocalDateTime fecha);
    List<Cliente> listarClientesRegistradosEntre(LocalDateTime inicio, LocalDateTime fin);
}