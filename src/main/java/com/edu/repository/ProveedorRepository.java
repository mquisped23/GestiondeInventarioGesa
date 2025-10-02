package com.edu.repository;

import com.edu.entity.EstadoProveedor;
import com.edu.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Buscar proveedor por RUC (para validar duplicados o búsquedas rápidas)
    Optional<Proveedor> findByRuc(String ruc);

    // Buscar proveedor por correo
    Optional<Proveedor> findByCorreo(String correo);

    // Buscar proveedor por nombre (coincidencia exacta)
    Optional<Proveedor> findByNombre(String nombre);

    // Buscar proveedores por nombre que contenga texto (para filtros o búsquedas parciales)
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

    // Buscar proveedores por estado (ej: mostrar solo activos)
    List<Proveedor> findByEstado(EstadoProveedor estado);

    // Buscar por teléfono
    Optional<Proveedor> findByTelefono(String telefono);

    // Validar si ya existe un RUC registrado
    boolean existsByRuc(String ruc);

    // Validar si ya existe un correo registrado
    boolean existsByCorreo(String correo);

    // Validar si ya existe un teléfono registrado
    boolean existsByTelefono(String telefono);

    // Búsqueda general en tabla proveedores
    @Query("SELECT p FROM Proveedor p " +
            "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.correo) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.ruc) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(CAST(p.estado AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Proveedor> buscarProveedores(@Param("keyword") String keyword, Pageable pageable);

    // Listar proveedores registrados después de una fecha específica (para reportes)
    List<Proveedor> findByFechaRegistroAfter(LocalDateTime fecha);

    // Listar proveedores registrados entre 2 fechas
    List<Proveedor> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);
}
