package com.edu.controller;

import com.edu.entity.Usuario;
import com.edu.entity.Venta;
import com.edu.service.ClienteService;
import com.edu.service.ProductoService;
import com.edu.service.UsuarioService;
import com.edu.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    private final ProductoService productoService;

    private final ClienteService clienteService;

    private final VentaService ventaService; // Inyecta el servicio de ventas

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String registrar(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        usuarioService.registrarUsuario(usuario, "USUARIO");
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/login"; // redirige al login (nuevo request)
    }


    @GetMapping("/index")
    public String index(Model model) {
        BigDecimal totalVentas = ventaService.calcularTotalVentas();
        //Obtenemos el valor total de stock
        Integer totalStock = productoService.SumaStock();
        //Obtenemos el total de clientes
        Integer totalClientes = clienteService.SumaListaClientes();
        //ultimas 3 ventas
        List<Venta> ultimasVentas = ventaService.obtenerUltimasVentas();

        // Agrega los productos más vendidos al modelo
        List<Map<String, Object>> productosMasVendidos = ventaService.obtenerProductosMasVendidos();


        // Agrega las ventas mensuales para el gráfico
        List<Map<String, Object>> ventasMensuales = ventaService.obtenerVentasMensuales();


        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("ultimasVentas", ultimasVentas);
        model.addAttribute("productosMasVendidos", productosMasVendidos);
        model.addAttribute("ventasMensuales", ventasMensuales);
        return "index";
    }
}