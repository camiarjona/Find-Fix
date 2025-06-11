package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.exception.exceptions.RolException;
import com.findfix.find_fix_app.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.usuario.dto.*;
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
    void eliminarPorId(Long id) throws UserNotFoundException;
    void actualizarPassword(ActualizarPasswordDTO actualizarPasswordDTO) throws UserNotFoundException;
    void actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO) throws UserNotFoundException;
    void actualizarRolesUsuario(Long idUsuario, ActualizarRolesUsuarioDTO usuarioRolesDTO) throws UserNotFoundException;
    void eliminarPorEmail(String email) throws UserNotFoundException;
    void actualizarUsuarioAdmin(ActualizarUsuarioDTO actualizarUsuarioDTO, String email) throws UserNotFoundException;
    void agregarRol(Usuario usuario, String nombreRol) throws UserNotFoundException, RolNotFoundException;
    VerPerfilUsuarioDTO verPerfilUsuario() throws UserNotFoundException;
    void actualizarUsuarioEspecialista(Usuario usuario);

}
