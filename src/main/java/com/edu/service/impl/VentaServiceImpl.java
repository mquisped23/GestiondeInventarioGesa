package com.edu.service.impl;

import com.edu.entity.*;
import com.edu.exception.BusinessException;
import com.edu.repository.UsuarioRepository;
import com.edu.repository.VentaRepository;
import com.edu.service.VentaService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;

    public VentaServiceImpl(VentaRepository ventaRepository, UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // === CRUD ===
    @Override
    public Venta registrarVenta(Venta venta) {
        if (venta.getCliente() == null) {
            throw new BusinessException("La venta debe tener un cliente asociado.");
        }

        if (venta.getProducto().getStock() < venta.getCantidad()) {
            throw new BusinessException("Stock insuficiente para el producto: "
                    + venta.getProducto().getNombre());
        }

        // Obtener usuario logueado desde Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado."));

        // Asignar el usuario logueado
        venta.setUsuario(usuario);

        // DEBEMOS TRAER EL PRECIO DEL PRODUCTO Y CALCULAR EL TOTAL ANTES DE VALIDAR
        // Convertimos el Integer a un BigDecimal para poder realizar la multiplicación
        BigDecimal cantidadBigDecimal = new BigDecimal(venta.getCantidad());
        // Multiplicamos el precio por la cantidad y asignamos el resultado
        venta.setTotal(venta.getProducto().getPrecio().multiply(cantidadBigDecimal));

        // Si pasa la validación, se descuenta:
        venta.getProducto().setStock(venta.getProducto().getStock() - venta.getCantidad());

        // Esta validación ahora funcionará, ya que total tiene un valor
        if (venta.getTotal() == null || venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El total de la venta debe ser mayor a 0.");
        }

        venta.setFechaVenta(LocalDateTime.now());

        return ventaRepository.save(venta);
    }

    @Override
    public Venta actualizarVenta(Long id, Venta venta) {
        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Venta no encontrada con id: " + id));

        if (venta.getCliente() == null) {
            throw new BusinessException("La venta debe tener un cliente asociado.");
        }

        if (venta.getUsuario() == null) {
            throw new BusinessException("La venta debe ser registrada por un usuario.");
        }

        if (venta.getTotal() == null || venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El total de la venta debe ser mayor a 0.");
        }

        // Actualizar datos
        existente.setCliente(venta.getCliente());
        existente.setUsuario(venta.getUsuario());
        existente.setTotal(venta.getTotal());
        existente.setEstado(venta.getEstado());
        existente.setProducto(venta.getProducto());
        existente.setCantidad(venta.getCantidad());

        return ventaRepository.save(existente);
    }

    @Override
    public void eliminarVenta(Long id) {
        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Venta no encontrada con id: " + id));

        // Eliminación lógica → ANULADA
        existente.setEstado(EstadoVenta.ANULADA);
        ventaRepository.save(existente);
    }

    @Override
    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    @Override
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public Page<Venta> listarVentas(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    @Override
    public Page<Venta> buscarVentas(String keyword, Pageable pageable) {
        return ventaRepository.buscarVentas(keyword, pageable);
    }

    // === Filtros ===
    @Override
    public List<Venta> obtenerPorUsuario(Usuario usuario) {
        return ventaRepository.findByUsuario(usuario);
    }

    @Override
    public List<Venta> obtenerPorCliente(Cliente cliente) {
        return ventaRepository.findByCliente(cliente);
    }

    @Override
    public List<Venta> obtenerPorProducto(Producto producto) {
        return ventaRepository.findByProducto(producto);
    }

    @Override
    public List<Venta> obtenerPorEstado(EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }

    // === Reportes ===
    @Override
    public List<Venta> listarVentasEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin);
    }

    @Override
    public List<Venta> listarVentasDespuesDe(LocalDateTime fecha) {
        return ventaRepository.findByFechaVentaAfter(fecha);
    }

    // === Negocio ===
    @Override
    public void cambiarEstado(Long id, EstadoVenta nuevoEstado) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Venta no encontrada con id: " + id));

        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new BusinessException("No se puede cambiar el estado de una venta ANULADA.");
        }

        venta.setEstado(nuevoEstado);
        ventaRepository.save(venta);
    }

    @Override
    public BigDecimal calcularTotalVentas() {
        return Optional.ofNullable(ventaRepository.sumarTotalVentas())
                .orElse(BigDecimal.ZERO);
    }

    //Obtenemos las 3 utimas ventas
    @Override
    public List<Venta> obtenerUltimasVentas() {
        return ventaRepository.findTop3ByOrderByFechaVentaDesc();
    }

    @Override
    public List<Map<String, Object>> obtenerProductosMasVendidos() {
        // Definimos una paginación que solo trae 3 resultados
        Pageable pageable = PageRequest.of(0, 3);

        // Llamamos al repositorio con la paginación
        List<Object[]> resultados = ventaRepository.findProductosMasVendidos(pageable);

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> ventaMap = new LinkedHashMap<>();
                    Producto producto = (Producto) result[0];
                    Long totalVendido = (Long) result[1];
                    ventaMap.put("producto", producto);
                    ventaMap.put("totalVendido", totalVendido);
                    return ventaMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> obtenerVentasMensuales() {
        List<Object[]> resultados = ventaRepository.findVentasMensuales();

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> ventaDiaria = new LinkedHashMap<>();

                    // Conversión de java.sql.Date a java.time.LocalDate
                    Date sqlDate = (Date) result[0];
                    LocalDate fecha = sqlDate.toLocalDate();

                    BigDecimal total = (BigDecimal) result[1];

                    ventaDiaria.put("fecha", fecha.format(DateTimeFormatter.ofPattern("dd")));
                    ventaDiaria.put("total", total);
                    return ventaDiaria;
                })
                .collect(Collectors.toList());
    }


}