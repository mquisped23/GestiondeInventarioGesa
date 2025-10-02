package com.edu.service.impl;

import com.edu.entity.Categoria;
import com.edu.entity.EstadoProducto;
import com.edu.entity.Producto;
import com.edu.entity.Proveedor;
import com.edu.exception.BusinessException;
import com.edu.repository.ProductoRepository;
import com.edu.service.ProductoService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // === CRUD ===
    @Override
    public Producto crearProducto(Producto producto) {
        if (existeNombre(producto.getNombre())) {
            throw new BusinessException("El nombre del producto ya está registrado: " + producto.getNombre());
        }

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor que 0.");
        }

        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new BusinessException("El stock no puede ser negativo.");
        }

        producto.setEstado(EstadoProducto.ACTIVO); // por defecto
        producto.setFechaRegistro(LocalDateTime.now());

        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizarProducto(Long id, Producto producto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado con id: " + id));

        if (!existente.getNombre().equals(producto.getNombre()) && existeNombre(producto.getNombre())) {
            throw new BusinessException("El nombre ya está registrado: " + producto.getNombre());
        }

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor que 0.");
        }

        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new BusinessException("El stock no puede ser negativo.");
        }

        // Actualizar campos
        existente.setNombre(producto.getNombre());
        existente.setDescripcion(producto.getDescripcion());
        existente.setCategoria(producto.getCategoria());
        existente.setProveedor(producto.getProveedor());
        existente.setPrecio(producto.getPrecio());
        existente.setStock(producto.getStock());
        existente.setEstado(producto.getEstado());

        return productoRepository.save(existente);
    }

    @Override
    public void eliminarProducto(Long id) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado con id: " + id));

        // Eliminación lógica
        existente.setEstado(EstadoProducto.INACTIVO);
        productoRepository.save(existente);
    }

    @Override
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Page<Producto> listarProductos(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    @Override
    public Page<Producto> buscarProductos(String keyword, Pageable pageable) {
        return productoRepository.buscarProductos(keyword, pageable);
    }

    // === Búsquedas ===
    @Override
    public Optional<Producto> obtenerPorNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    @Override
    public List<Producto> buscarPorNombre(String nombreParcial) {
        return productoRepository.findByNombreContainingIgnoreCase(nombreParcial);
    }

    @Override
    public List<Producto> obtenerPorEstado(EstadoProducto estado) {
        return productoRepository.findByEstado(estado);
    }

    @Override
    public List<Producto> obtenerPorCategoria(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Producto> obtenerPorProveedor(Proveedor proveedor) {
        return productoRepository.findByProveedor(proveedor);
    }

    // === Validaciones ===
    @Override
    public boolean existeNombre(String nombre) {
        return productoRepository.existsByNombre(nombre);
    }

    // === Reportes ===
    @Override
    public List<Producto> listarProductosRegistradosDespuesDe(LocalDateTime fecha) {
        return productoRepository.findByFechaRegistroAfter(fecha);
    }

    @Override
    public List<Producto> listarProductosRegistradosEntre(LocalDateTime inicio, LocalDateTime fin) {
        return productoRepository.findByFechaRegistroBetween(inicio, fin);
    }

    @Override
    public Integer SumaStock() {
        return Optional.ofNullable(productoRepository.SumaStock())
                .orElse(0);
    }

    // Cambiar estado (activo ↔ inactivo)
    @Override
    public void cambiarEstado(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado con id: " + id));

        if (producto.getEstado() == null) {
            producto.setEstado(EstadoProducto.ACTIVO);
        }

        if (producto.getEstado() == EstadoProducto.ACTIVO) {
            producto.setEstado(EstadoProducto.INACTIVO);
        } else {
            producto.setEstado(EstadoProducto.ACTIVO);
        }

        productoRepository.save(producto);
    }
}
