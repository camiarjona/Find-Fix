package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.usuario.dto.ActualizarUsuarioDTO;
import com.findfix.find_fix_app.usuario.dto.BuscarUsuarioDTO;
import com.findfix.find_fix_app.usuario.dto.MostrarUsuarioDTO;
import com.findfix.find_fix_app.usuario.dto.VerPerfilUsuarioDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    //metodo para ver la lista de usuarios registrados
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<VerPerfilUsuarioDTO>> obtenerUsuarios(
        @RequestParam(required = false) String rolId,
        @PageableDefault(size = 10) Pageable pageable) {
    return ResponseEntity.ok(usuarioService.obtenerUsuarios(rolId, pageable));
}

    //metodo para modificar un usuario (desde admin)
    @PatchMapping("/modificar/{email}") //CHEQUEADO
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> actualizarUsuarioAdmin(@Valid @RequestBody ActualizarUsuarioDTO usuario, @PathVariable String email) throws UsuarioNotFoundException {
        usuarioService.actualizarUsuarioAdmin(usuario, email);
        return ResponseEntity.ok(new ApiResponse<>("Modificaciones realizadas con éxito☑️", "Consulte la lista de usuarios para corroborar los cambios."));
    }

    @PostMapping("/filtrar")
    @PreAuthorize("hasRole('ADMIN')") //CHEQUEADO
    public ResponseEntity<ApiResponse<List<VerPerfilUsuarioDTO>>> filtrarUsuarios(@RequestBody BuscarUsuarioDTO filtro) throws UsuarioNotFoundException, UsuarioException {
        List<Usuario> usuariosFiltrados = usuarioService.filtrarUsuarios(filtro);
        return ResponseEntity.ok(new ApiResponse<>("Coincidencias⬇️", usuariosFiltrados.stream().map(VerPerfilUsuarioDTO::new).toList()));
    }

    @PatchMapping("/desactivar/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> desactivarUsuario(@PathVariable String email) throws UsuarioNotFoundException {
        usuarioService.desactivarUsuario(email);
        return ResponseEntity.ok(new ApiResponse<>("Usuario desactivado", email));
    }

    @PatchMapping("/activar/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activarUsuario(@PathVariable String email) throws UsuarioNotFoundException {
        usuarioService.activarUsuario(email);
        return ResponseEntity.ok(new ApiResponse<>("Usuario reactivado", email));
    }

    @DeleteMapping("/eliminar/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> eliminarUsuario(@PathVariable String email) throws UsuarioNotFoundException, RolNotFoundException, EspecialistaNotFoundException {
       usuarioService.eliminarCuentaPorEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Usuario eliminado con éxito✅", "{}"));
    }
}
