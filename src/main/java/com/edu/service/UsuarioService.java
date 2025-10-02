package com.edu.service;

import com.edu.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    Usuario registrarUsuario(Usuario usuario, String nombreRol);
    Usuario buscarPorUsername(String username);
    List<Usuario> listarUsuarios();
}