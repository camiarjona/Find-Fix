package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.usuario.dto.ActualizarPasswordDTO;
import com.findfix.find_fix_app.usuario.dto.ActualizarUsuarioDTO;
import com.findfix.find_fix_app.usuario.dto.RegistroDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<?> mostrarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/registrar")
    public ResponseEntity<RegistroDTO> registrarUsuario(@Valid @RequestBody RegistroDTO registro) {
        usuarioService.registrarNuevoUsuario(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(registro);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarUsuario(Long id) throws UserNotFoundException {
        usuarioService.eliminar(id);
        return ResponseEntity.ok("Usuario eliminado con éxito.");
    }

    @PatchMapping("/modificar-datos")
    public ResponseEntity<String> modificarUsuario(@Valid @RequestBody ActualizarUsuarioDTO usuario) throws UserNotFoundException {
        usuarioService.actualizarUsuario(usuario);
        return ResponseEntity.ok("Modificaciones realizadas con éxito\n" + usuario);
    }

    @PatchMapping("/modificar-password")
    public ResponseEntity<String> modificarPassword(@Valid @RequestBody ActualizarPasswordDTO passwordDTO) throws UserNotFoundException {
        usuarioService.actualizarPassword(passwordDTO);
        return ResponseEntity.ok("Password modificado con éxito");
    }

}
