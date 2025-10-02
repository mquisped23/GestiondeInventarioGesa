package com.edu.service;

import com.edu.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VentaService {

    // === Operaciones CRUD ===
    Venta registrarVenta(Venta venta);
    Venta actualizarVenta(Long id, Venta venta);
    void eliminarVenta(Long id);
    Optional<Venta> obtenerVentaPorId(Long id);
    List<Venta> listarVentas();

    // === Operaciones con paginación y búsqueda ===
    Page<Venta> listarVentas(Pageable pageable);
    Page<Venta> buscarVentas(String keyword, Pageable pageable);

    // === Filtros específicos ===
    List<Venta> obtenerPorUsuario(Usuario usuario);
    List<Venta> obtenerPorCliente(Cliente cliente);
    List<Venta> obtenerPorProducto(Producto producto);
    List<Venta> obtenerPorEstado(EstadoVenta estado);

    // === Reportes ===
    List<Venta> listarVentasEntreFechas(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> listarVentasDespuesDe(LocalDateTime fecha);

    // === Operaciones de negocio ===
    void cambiarEstado(Long id, EstadoVenta nuevoEstado); // Ej: PENDIENTE → PAGADA → ANULADA

    // Nueva operación de dashboard/reporte
    BigDecimal calcularTotalVentas();
    //Obtenemos las ultimas 3 ventas
    List<Venta> obtenerUltimasVentas();

    // Nueva operación de dashboard para los productos más vendidos
    List<Map<String, Object>> obtenerProductosMasVendidos();

    // Nueva operación de dashboard para el gráfico de ventas
    List<Map<String, Object>> obtenerVentasMensuales();
}
