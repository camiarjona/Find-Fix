package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.usuario.dto.RegistroDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UsuarioService {
    Optional<Usuario> encontrarPorEmail(String email);
    List<Usuario> obtenerUsuarios();
    Optional<Usuario> encontrarPorId(Long id);
    Usuario guardar(RegistroDTO registroDTO);

}
