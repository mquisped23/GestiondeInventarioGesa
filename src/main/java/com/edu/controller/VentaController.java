package com.edu.controller;

import com.edu.entity.EstadoVenta;
import com.edu.entity.Venta;
import com.edu.service.*;
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
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private PdfService pdfService;

    /**
     * LISTAR VENTAS CON PAGINACIÓN Y BÚSQUEDA
     */
    @GetMapping
    public String listarVentas(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Venta> ventasPage;

        if (keyword != null && !keyword.isEmpty()) {
            ventasPage = ventaService.buscarVentas(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            ventasPage = ventaService.listarVentas(pageable);
        }

        model.addAttribute("nuevaVenta", new Venta()); // para modal de registro
        model.addAttribute("ventasPage", ventasPage);
        model.addAttribute("currentPage", page);

        // listas necesarias para selects
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("estados", EstadoVenta.values());

        return "ventas";
    }

    /**
     * REGISTRAR VENTA
     */
    @PostMapping("/registrar")
    public String registrarVenta(
            @Valid @ModelAttribute("nuevaVenta") Venta venta,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos. Verifica el formulario.");
            return "redirect:/ventas";
        }

        try {
            ventaService.registrarVenta(venta);
            redirectAttributes.addFlashAttribute("success", "Venta registrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la venta: " + e.getMessage());
        }

        return "redirect:/ventas";
    }

    /**
     * ACTUALIZAR VENTA
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarVenta(
            @PathVariable Long id,
            @Valid @ModelAttribute("venta") Venta venta,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos al actualizar la venta.");
            return "redirect:/ventas";
        }

        try {
            ventaService.actualizarVenta(id, venta);
            redirectAttributes.addFlashAttribute("success", "Venta actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la venta: " + e.getMessage());
        }

        return "redirect:/ventas";
    }

    /**
     * ELIMINAR VENTA (lógica → estado = ANULADA)
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarVenta(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ventaService.eliminarVenta(id);
            redirectAttributes.addFlashAttribute("success", "Venta anulada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo anular la venta: " + e.getMessage());
        }

        return "redirect:/ventas";
    }



    @PostMapping("/exportar-pdf")
    public ResponseEntity<InputStreamResource> exportarVentasPorFechas(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) throws Exception {

        LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime finDateTime = fechaFin.atTime(23, 59, 59);

        List<Venta> ventas = ventaService.listarVentasEntreFechas(inicioDateTime, finDateTime);

        if (ventas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        Context context = new Context();
        context.setVariable("ventas", ventas);
        String html = templateEngine.process("pdf/ventas-pdf", context);


        ByteArrayInputStream pdfStream = pdfService.generarPdfDesdeHtml(html);

        String filename = "ventas_" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}