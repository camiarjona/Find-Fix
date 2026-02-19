package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.utils.exception.exceptions.*;
import com.findfix.find_fix_app.usuario.dto.*;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public interface UsuarioService {
    boolean tieneRol(Usuario usuario, String rol);
    List<Usuario> filtrarUsuarios(BuscarUsuarioDTO filtro) throws UsuarioNotFoundException, UsuarioException;
    Page<VerPerfilUsuarioDTO> obtenerUsuarios(String nombreRol, Pageable pageable);
    void actualizarPassword(ActualizarPasswordDTO actualizarPasswordDTO) throws UsuarioNotFoundException;
    void actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO) throws UsuarioNotFoundException;
    void eliminarPorEmail(String email) throws UsuarioNotFoundException;
    Optional<Usuario> obtenerUsuarioPorEmail(String email);
    void actualizarUsuarioAdmin(ActualizarUsuarioDTO actualizarUsuarioDTO, String email) throws UsuarioNotFoundException;
    void agregarRol(Usuario usuario, String nombreRol) throws RolNotFoundException;
    void eliminarRol(Usuario usuario, String nombreRol) throws RolNotFoundException;
    VerPerfilUsuarioDTO verPerfilUsuario() throws UsuarioNotFoundException;
    void actualizarUsuarioEspecialista(Usuario usuario);
    void desactivarUsuario(String email) throws UsuarioNotFoundException;
    void activarUsuario(String email) throws UsuarioNotFoundException;
    void eliminarCuentaPorEmail(String email) throws UsuarioNotFoundException, EspecialistaNotFoundException, RolNotFoundException;
}
