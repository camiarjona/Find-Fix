package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.exception.exceptions.RolException;
import com.findfix.find_fix_app.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.usuario.dto.ActualizarPasswordDTO;
import com.findfix.find_fix_app.usuario.dto.ActualizarRolesUsuarioDTO;
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
    void registrarNuevoUsuario(RegistroDTO registroDTO) throws RolException, UserException;
    void eliminar(Long id) throws UserNotFoundException;
    void actualizarPassword(ActualizarPasswordDTO actualizarPasswordDTO) throws UserNotFoundException;
    void actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO) throws UserNotFoundException;
    void actualizarRolesUsuario(Long idUsuario, ActualizarRolesUsuarioDTO usuarioRolesDTO) throws UserNotFoundException;
}
