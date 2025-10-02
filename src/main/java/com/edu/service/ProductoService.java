package com.edu.service;

import com.edu.entity.Categoria;
import com.edu.entity.EstadoProducto;
import com.edu.entity.Producto;
import com.edu.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    // === Operaciones CRUD ===
    Producto crearProducto(Producto producto);
    Producto actualizarProducto(Long id, Producto producto);
    void eliminarProducto(Long id);
    Optional<Producto> obtenerProductoPorId(Long id);
    List<Producto> listarProductos();

    // === Operaciones de búsqueda ===
    Page<Producto> listarProductos(Pageable pageable);
    Page<Producto> buscarProductos(String keyword, Pageable pageable);
    Optional<Producto> obtenerPorNombre(String nombre);
    List<Producto> buscarPorNombre(String nombreParcial);
    List<Producto> obtenerPorEstado(EstadoProducto estado);
    List<Producto> obtenerPorCategoria(Categoria categoria);
    List<Producto> obtenerPorProveedor(Proveedor proveedor);

    // Cambiar estado (activo ↔ inactivo)
    void cambiarEstado(Long id);

    // === Validaciones ===
    boolean existeNombre(String nombre);

    // === Reportes ===
    List<Producto> listarProductosRegistradosDespuesDe(LocalDateTime fecha);
    List<Producto> listarProductosRegistradosEntre(LocalDateTime inicio, LocalDateTime fin);

    //Suma de stock producto
    Integer SumaStock();
}