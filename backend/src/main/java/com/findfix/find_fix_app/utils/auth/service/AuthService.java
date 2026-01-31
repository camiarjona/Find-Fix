package com.findfix.find_fix_app.utils.auth.service;

import com.findfix.find_fix_app.utils.auth.dto.AuthResponseDTO;
import com.findfix.find_fix_app.utils.auth.dto.RegistroDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.auth.dto.TokenRefreshResponseDTO;
import com.findfix.find_fix_app.utils.auth.dto.UsuarioLoginDTO;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;

public interface AuthService {
    String obtenerEmailUsuarioAutenticado();
    Usuario obtenerUsuarioAutenticado() throws UsuarioNotFoundException;
    AuthResponseDTO login(UsuarioLoginDTO loginDTO) throws UsuarioNotFoundException;
    Usuario registrarNuevoUsuario(RegistroDTO registroDTO) throws RolException, UsuarioException;
    TokenRefreshResponseDTO refrescarToken(String refreshTokenStr);
}
