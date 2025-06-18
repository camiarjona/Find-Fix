package com.findfix.find_fix_app.usuario.service.delete;

import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteService {

    private final UsuarioService usuarioService;
    private final EspecialistaService especialistaService;

    @Transactional(rollbackFor = Exception.class)
    public void eliminarCuentaPorEmail(String email) throws UsuarioNotFoundException, EspecialistaNotFoundException, RolNotFoundException {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado."));

        if (usuarioService.tieneRol(usuario,"ESPECIALISTA")) {
            especialistaService.eliminarPorEmail(email);
        }

        usuarioService.eliminarPorEmail(email);
    }
}
