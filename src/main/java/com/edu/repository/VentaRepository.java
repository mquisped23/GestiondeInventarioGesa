package com.edu.repository;

import com.edu.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Buscar ventas por usuario (quién registró la venta)
    List<Venta> findByUsuario(Usuario usuario);

    // Buscar ventas por cliente
    List<Venta> findByCliente(Cliente cliente);

    // Buscar ventas por producto
    List<Venta> findByProducto(Producto producto);

    // Buscar ventas por estado (PAGADA, PENDIENTE, ANULADA)
    List<Venta> findByEstado(EstadoVenta estado);

    // Buscar ventas entre fechas (útil para reportes)
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Búsqueda general (por cliente, usuario o estado)
    @Query("SELECT v FROM Venta v " +
            "WHERE LOWER(v.cliente.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(v.usuario.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(v.producto.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Venta> buscarVentas(@Param("keyword") String keyword, Pageable pageable);

    // Ventas registradas después de una fecha
    List<Venta> findByFechaVentaAfter(LocalDateTime fecha);

    // Nueva consulta para sumar el total de ventas
    //no tomaremos en cuenta las ventaas anuladas
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.estado <> 'ANULADA'")
    BigDecimal sumarTotalVentas();

    //Obtenemos las ultimas 3 ventas
    List<Venta> findTop3ByOrderByFechaVentaDesc();

    // Nueva consulta para obtener los productos más vendidos
    @Query("SELECT v.producto, SUM(v.cantidad) AS total_vendido " +
            "FROM Venta v " +
            "WHERE v.estado <> 'ANULADA' " +
            "GROUP BY v.producto " +
            "ORDER BY total_vendido DESC")
    List<Object[]> findProductosMasVendidos(Pageable pageable); //

    // Nueva consulta para obtener las ventas diarias del mes actual
    @Query("SELECT FUNCTION('DATE', v.fechaVenta) as fecha, SUM(v.total) as totalDiario " +
            "FROM Venta v " +
            "WHERE FUNCTION('YEAR', v.fechaVenta) = FUNCTION('YEAR', CURRENT_DATE()) " +
            "AND FUNCTION('MONTH', v.fechaVenta) = FUNCTION('MONTH', CURRENT_DATE()) " +
            "AND v.estado <> 'ANULADA' " +
            "GROUP BY fecha " +
            "ORDER BY fecha ASC")
    List<Object[]> findVentasMensuales();
}