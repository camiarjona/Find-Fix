package com.findfix.find_fix_app.notificacion.controller;

import com.findfix.find_fix_app.notificacion.model.Notificacion;
import com.findfix.find_fix_app.notificacion.service.NotificacionService;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.auth.service.AuthService;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones") 
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final AuthService authService;

    @GetMapping("/mis-notificaciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<Notificacion>>> obtenerMisNotificaciones() throws UsuarioNotFoundException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();
        List<Notificacion> notificaciones = notificacionService.obtenerMisNotificaciones(usuario);
        return ResponseEntity.ok(new ApiResponse<>("Notificaciones obtenidas con éxito", notificaciones));
    }

    @PatchMapping("/{id}/leida")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok(new ApiResponse<>("Notificación actualizada", "Marcada como leída correctamente"));
    }
}