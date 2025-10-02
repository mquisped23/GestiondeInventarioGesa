package com.edu.repository;

import com.edu.entity.Cliente;
import com.edu.entity.EstadoCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar cliente por correo (para validar duplicados o búsquedas rápidas)
    Optional<Cliente> findByCorreo(String correo);

    // Buscar clientes por nombre (coincidencia exacta)
    Optional<Cliente> findByNombre(String nombre);

    // Buscar clientes por nombre conteniendo texto (para filtros y búsquedas)
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    // Buscar clientes por estado (ej: mostrar solo activos)
    List<Cliente> findByEstado(EstadoCliente estado);

    // Buscar por teléfono
    Optional<Cliente> findByTelefono(String telefono);

    // Verificar si ya existe un correo registrado (para validaciones de registro)
    boolean existsByCorreo(String correo);

    // Verificar si ya existe un teléfono registrado (si es necesario)
    boolean existsByTelefono(String telefono);

    @Query("SELECT c FROM Cliente c " +
            "WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(c.correo) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(CAST(c.estado AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Cliente> buscarClientes(@Param("keyword") String keyword, Pageable pageable);

    // Listar clientes registrados después de una fecha específica (para reportes)
    List<Cliente> findByFechaRegistroAfter(LocalDateTime fecha);

    // Listar clientes registrados entre 2 fechas
    List<Cliente> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);
}