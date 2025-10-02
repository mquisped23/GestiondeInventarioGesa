package com.edu.controller;

import com.edu.entity.Cliente;
import com.edu.service.ClienteService;
import com.edu.service.PdfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private PdfService pdfService;

    /**
     * LISTAR CLIENTES
     */
   /* @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("nuevoCliente", new Cliente()); // para el modal de registro
        return "clientes"; // se renderizará con layout.html
    }*/

    @GetMapping
    public String listarClientes(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, 10); // 10 registros por página
        Page<Cliente> clientesPage;

        if (keyword != null && !keyword.isEmpty()) {
            clientesPage = clienteService.buscarClientes(keyword, pageable);
            model.addAttribute("keyword", keyword); // para que el input mantenga el valor
        } else {
            clientesPage = clienteService.listarClientes(pageable);
        }

        model.addAttribute("nuevoCliente", new Cliente()); // para el modal de registro
        model.addAttribute("clientesPage", clientesPage);
        model.addAttribute("currentPage", page);

        return "clientes"; // tu vista con la tabla
    }

    /**
     * REGISTRAR CLIENTE
     */
    @PostMapping("/registrar")
    public String registrarCliente(
            @Valid @ModelAttribute("nuevoCliente") Cliente cliente,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos. Verifica el formulario.");
            return "redirect:/clientes";
        }

        try {
            clienteService.crearCliente(cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el cliente: " + e.getMessage());
        }

        return "redirect:/clientes";
    }

    /**
     * EDITAR CLIENTE
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarCliente(
            @PathVariable Long id,
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos al actualizar el cliente.");
            return "redirect:/clientes";
        }

        try {
            clienteService.actualizarCliente(id, cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el cliente: " + e.getMessage());
        }

        return "redirect:/clientes";
    }

    /**
     * ELIMINAR CLIENTE
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            clienteService.eliminarCliente(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el cliente: " + e.getMessage());
        }

        return "redirect:/clientes";
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
            clienteService.cambiarEstado(id);
            redirectAttributes.addFlashAttribute("success", "Estado del cliente actualizado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cambiar el estado: " + e.getMessage());
        }

        return "redirect:/clientes";
    }


    @PostMapping("/exportar-pdf")
    public ResponseEntity<InputStreamResource> exportarClientesPorFechas(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) throws Exception {

        LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime finDateTime = fechaFin.atTime(23, 59, 59);

        List<Cliente> clientes = clienteService.listarClientesRegistradosEntre(inicioDateTime, finDateTime);

        if (clientes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        Context context = new Context();
        context.setVariable("clientes", clientes);
        String html = templateEngine.process("pdf/clientes-pdf", context);


        ByteArrayInputStream pdfStream = pdfService.generarPdfDesdeHtml(html);

        String filename = "clientes_" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}