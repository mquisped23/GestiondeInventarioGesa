package com.edu.controller;

import com.edu.entity.Categoria;
import com.edu.entity.Producto;
import com.edu.service.ProductoService;
import com.edu.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProveedorService proveedorService;

    /**
     * LISTAR PRODUCTOS CON PAGINACIÓN Y BÚSQUEDA
     */
    @GetMapping
    public String listarProductos(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Producto> productosPage;

        if (keyword != null && !keyword.isEmpty()) {
            productosPage = productoService.buscarProductos(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            productosPage = productoService.listarProductos(pageable);
        }

        model.addAttribute("nuevoProducto", new Producto()); // para modal de registro
        model.addAttribute("productosPage", productosPage);
        model.addAttribute("currentPage", page);

        // listas necesarias para selects
        model.addAttribute("proveedores", proveedorService.listarProveedores());
        model.addAttribute("categorias", Categoria.values());

        return "productos";
    }

    /**
     * REGISTRAR PRODUCTO
     */
    @PostMapping("/registrar")
    public String registrarProducto(
            @Valid @ModelAttribute("nuevoProducto") Producto producto,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos. Verifica el formulario.");
            return "redirect:/productos";
        }

        try {
            productoService.crearProducto(producto);
            redirectAttributes.addFlashAttribute("success", "Producto registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el producto: " + e.getMessage());
        }

        return "redirect:/productos";
    }

    /**
     * ACTUALIZAR PRODUCTO
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarProducto(
            @PathVariable Long id,
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos al actualizar el producto.");
            return "redirect:/productos";
        }

        try {
            productoService.actualizarProducto(id, producto);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
        }

        return "redirect:/productos";
    }

    /**
     * ELIMINAR PRODUCTO
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el producto: " + e.getMessage());
        }

        return "redirect:/productos";
    }
}