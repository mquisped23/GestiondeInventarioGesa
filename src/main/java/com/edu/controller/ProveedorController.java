package com.edu.controller;

import com.edu.entity.Proveedor;
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
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    /**
     * LISTAR PROVEEDORES CON PAGINACIÓN Y BÚSQUEDA
     */
    @GetMapping
    public String listarProveedores(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Proveedor> proveedoresPage;

        if (keyword != null && !keyword.isEmpty()) {
            proveedoresPage = proveedorService.buscarProveedores(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            proveedoresPage = proveedorService.listarProveedores(pageable);
        }

        model.addAttribute("nuevoProveedor", new Proveedor()); // para el modal de registro
        model.addAttribute("proveedoresPage", proveedoresPage);
        model.addAttribute("currentPage", page);

        return "proveedores"; // vista con la tabla
    }

    /**
     * REGISTRAR PROVEEDOR
     */
    @PostMapping("/registrar")
    public String registrarProveedor(
            @Valid @ModelAttribute("nuevoProveedor") Proveedor proveedor,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {

            if (proveedor.getRuc().length() < 11) {
                redirectAttributes.addFlashAttribute("error", "El ruc debe ser de 11 caracteres");
            }else{
            redirectAttributes.addFlashAttribute("error", "Datos inválidos. Verifica el formulario.");
            }
            return "redirect:/proveedores";
        }

        try {
            proveedorService.crearProveedor(proveedor);
            redirectAttributes.addFlashAttribute("success", "Proveedor registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el proveedor: " + e.getMessage());
        }

        return "redirect:/proveedores";
    }

    /**
     * EDITAR PROVEEDOR
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarProveedor(
            @PathVariable Long id,
            @Valid @ModelAttribute("proveedor") Proveedor proveedor,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos al actualizar el proveedor.");
            return "redirect:/proveedores";
        }

        try {
            proveedorService.actualizarProveedor(id, proveedor);
            redirectAttributes.addFlashAttribute("success", "Proveedor actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el proveedor: " + e.getMessage());
        }

        return "redirect:/proveedores";
    }

    /**
     * ELIMINAR PROVEEDOR
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarProveedor(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            proveedorService.eliminarProveedor(id);
            redirectAttributes.addFlashAttribute("success", "Proveedor eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el proveedor: " + e.getMessage());
        }

        return "redirect:/proveedores";
    }

    /**
     * CAMBIAR ESTADO (ACTIVO / INACTIVO)
     */
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            proveedorService.cambiarEstado(id);
            redirectAttributes.addFlashAttribute("success", "Estado del proveedor actualizado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cambiar el estado: " + e.getMessage());
        }

        return "redirect:/proveedores";
    }
}