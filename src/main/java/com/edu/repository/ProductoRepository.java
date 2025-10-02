package com.edu.repository;

import com.edu.entity.Categoria;
import com.edu.entity.EstadoProducto;
import com.edu.entity.Producto;
import com.edu.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar producto por nombre (exacto)
    Optional<Producto> findByNombre(String nombre);

    // Buscar productos por nombre que contengan texto (para filtros o búsquedas parciales)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por estado (ej: activos / inactivos)
    List<Producto> findByEstado(EstadoProducto estado);

    // Buscar productos por categoría
    List<Producto> findByCategoria(Categoria categoria);

    // Buscar productos por proveedor
    List<Producto> findByProveedor(Proveedor proveedor);

    // Validar si ya existe un producto con un nombre (para evitar duplicados)
    boolean existsByNombre(String nombre);



    // Búsqueda general en tabla productos
    @Query("SELECT p FROM Producto p " +
            "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(CAST(p.categoria AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(CAST(p.estado AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.proveedor.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Producto> buscarProductos(@Param("keyword") String keyword, Pageable pageable);

    // Listar productos registrados después de una fecha específica (para reportes)
    List<Producto> findByFechaRegistroAfter(LocalDateTime fecha);

    // Listar productos registrados entre 2 fechas
    List<Producto> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);

    //Sumamos el estock de productos
    @Query("SELECT SUM(p.stock) FROM Producto p ")
    Integer SumaStock();
}