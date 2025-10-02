package com.edu.service;

import com.edu.entity.EstadoProveedor;
import com.edu.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProveedorService {

    // === Operaciones CRUD ===
    Proveedor crearProveedor(Proveedor proveedor);
    Proveedor actualizarProveedor(Long id, Proveedor proveedor);
    void eliminarProveedor(Long id);
    Optional<Proveedor> obtenerProveedorPorId(Long id);
    List<Proveedor> listarProveedores();

    // === Operaciones de búsqueda ===
    Page<Proveedor> listarProveedores(Pageable pageable);
    Page<Proveedor> buscarProveedores(String keyword, Pageable pageable);
    Optional<Proveedor> obtenerPorRuc(String ruc);
    Optional<Proveedor> obtenerPorCorreo(String correo);
    Optional<Proveedor> obtenerPorNombre(String nombre);
    List<Proveedor> buscarPorNombre(String nombreParcial);
    List<Proveedor> obtenerPorEstado(EstadoProveedor estado);
    Optional<Proveedor> obtenerPorTelefono(String telefono);

    // Cambiar estado (activo ↔ inactivo)
    void cambiarEstado(Long id);

    // === Validaciones ===
    boolean existeRuc(String ruc);
    boolean existeCorreo(String correo);
    boolean existeTelefono(String telefono);

    // === Reportes ===
    List<Proveedor> listarProveedoresRegistradosDespuesDe(LocalDateTime fecha);
    List<Proveedor> listarProveedoresRegistradosEntre(LocalDateTime inicio, LocalDateTime fin);
}