package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.usuario.dto.ActualizarPasswordDTO;
import com.findfix.find_fix_app.usuario.dto.ActualizarUsuarioDTO;
import com.findfix.find_fix_app.usuario.dto.RegistroDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UsuarioService {
    Optional<Usuario> buscarPorEmail(String email);
    List<Usuario> obtenerUsuarios();
    Optional<Usuario> buscarPorId(Long id);
    Usuario registrarNuevoUsuario(RegistroDTO registroDTO);
    void eliminar(Long id);
    void actualizarPassword(ActualizarPasswordDTO actualizarPasswordDTO);
    void actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO);
}
