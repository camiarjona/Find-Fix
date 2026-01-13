package com.findfix.find_fix_app.utils.auth.service;

import com.findfix.find_fix_app.utils.auth.dto.RegistroDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.auth.dto.UsuarioLoginDTO;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

    String obtenerEmailUsuarioAutenticado();
    Usuario obtenerUsuarioAutenticado() throws UsuarioNotFoundException;

    // LOGIN: Autentica y devuelve el Usuario
    Usuario login(UsuarioLoginDTO loginDTO) throws UsuarioNotFoundException;

    // metodo para registrar un nuevo user
    @Transactional(rollbackFor = Exception.class)
    Usuario registrarNuevoUsuario(RegistroDTO registroDTO) throws RolException, UsuarioException;
}
