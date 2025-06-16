package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.exception.exceptions.RolException;
import com.findfix.find_fix_app.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.usuario.dto.*;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsuarioService {
    List<Usuario> filtrarUsuarios(BuscarUsuarioDTO filtro) throws UserNotFoundException, UserException;
    List<Usuario> obtenerUsuarios();
    void registrarNuevoUsuario(RegistroDTO registroDTO) throws RolException, UserException;
    void eliminarPorId(Long id) throws UserNotFoundException;
    void actualizarPassword(ActualizarPasswordDTO actualizarPasswordDTO) throws UserNotFoundException;
    void actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO) throws UserNotFoundException;
    void actualizarRolesUsuario(String email, ActualizarRolesUsuarioDTO usuarioRolesDTO) throws UserNotFoundException;
    void eliminarPorEmail(String email) throws UserNotFoundException;
    void actualizarUsuarioAdmin(ActualizarUsuarioDTO actualizarUsuarioDTO, String email) throws UserNotFoundException;
    void agregarRol(Usuario usuario, String nombreRol) throws RolNotFoundException;
    VerPerfilUsuarioDTO verPerfilUsuario() throws UserNotFoundException;
    void actualizarUsuarioEspecialista(Usuario usuario);
    List<String> ciudadesDisponibles();

}
