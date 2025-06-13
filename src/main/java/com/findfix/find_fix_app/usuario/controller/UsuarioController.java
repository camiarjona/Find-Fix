package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.exception.exceptions.RolException;
import com.findfix.find_fix_app.exception.exceptions.UserException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.usuario.dto.*;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/ver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> obtenerUsuarios() throws UserException {
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        //AGREGAR MOSTRAR DTO
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/registrar") //CHEQUEADO
    public ResponseEntity<Map<String, String>> registrarUsuario(@Valid @RequestBody RegistroDTO registro) throws UserException, RolException {
        usuarioService.registrarNuevoUsuario(registro);
        Map<String, String> response = new HashMap<>();
        response.put("Usuario registrado con éxito✅", "\nNombre: " + registro.nombre() + "\nApellido: " + registro.apellido() + "\nEmail: " +  registro.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/eliminar-por-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) throws UserNotFoundException {
        usuarioService.eliminarPorId(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario eliminado con éxito✅");
    }

    @DeleteMapping("/eliminar-por-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarUsuarioPorEmail(@PathVariable String email) throws UserNotFoundException {
        usuarioService.eliminarPorEmail(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario eliminado con éxito✅");
    }

    @GetMapping("/obtener-por-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MostrarUsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id) throws UserNotFoundException {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + id + " no encontrado."));

        MostrarUsuarioDTO usuarioDTO = new MostrarUsuarioDTO(usuario);
        return ResponseEntity.ok()
                .body(usuarioDTO);
    }

    //metodo para que el usuario pueda modificar sus datos
    @PatchMapping("/modificar-datos") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA', 'CLIENTE')")
    public ResponseEntity<String> actualizarUsuario(@Valid @RequestBody ActualizarUsuarioDTO usuario) throws UserNotFoundException {
        usuarioService.actualizarUsuario(usuario);
        return ResponseEntity.ok("Modificaciones realizadas con éxito☑️\n" + usuario);
    }

    //metodo para modificar un usuario (desde admin)
    @PatchMapping("/modificar/{email}") //CHEQUEADO
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> actualizarUsuarioAdmin(@Valid @RequestBody ActualizarUsuarioDTO usuario, @PathVariable String email) throws UserNotFoundException {
        usuarioService.actualizarUsuarioAdmin(usuario, email);
        return ResponseEntity.ok("Modificaciones realizadas con éxito☑️\n" + usuario);
    }

    @PatchMapping("/modificar-password") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA', 'CLIENTE')")
    public ResponseEntity<String> modificarPassword(@Valid @RequestBody ActualizarPasswordDTO passwordDTO) throws UserNotFoundException {
        usuarioService.actualizarPassword(passwordDTO);
        return ResponseEntity.ok("Password modificado con éxito☑️");
    }

    //admin
    @PatchMapping("/actualizar-roles/{idUsuario}") //CHEQUEADO
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, ActualizarRolesUsuarioDTO>> actualizarRoles(@PathVariable Long idUsuario, @Valid @RequestBody ActualizarRolesUsuarioDTO rolesDTO) throws UserNotFoundException {
        usuarioService.actualizarRolesUsuario(idUsuario, rolesDTO);
        Map<String, ActualizarRolesUsuarioDTO> response = new HashMap<>();
        response.put("Roles actualizados con éxito☑️", rolesDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/ver-perfil") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA', 'CLIENTE')")
    public ResponseEntity<Map<String, VerPerfilUsuarioDTO>> verPerfilUsuario() throws UserNotFoundException {
        Map<String, VerPerfilUsuarioDTO> response = new HashMap<>();
        VerPerfilUsuarioDTO usuarioDTO = usuarioService.verPerfilUsuario();
        response.put("Perfil:", usuarioDTO);
        return ResponseEntity.ok(response);
    }

}
