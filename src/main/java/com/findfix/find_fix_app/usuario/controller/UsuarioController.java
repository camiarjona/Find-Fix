package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.enums.CiudadesDisponibles;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerUsuarios() throws UserException {
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de usuarios encontrada️☑️️");
        response.put("Usuarios", usuarios.stream().map(MostrarUsuarioDTO::new).toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar") //CHEQUEADO
    public ResponseEntity<Map<String, String>> registrarUsuario(@Valid @RequestBody RegistroDTO registro) throws UserException, RolException {
        usuarioService.registrarNuevoUsuario(registro);
        Map<String, String> response = new HashMap<>();
        response.put("Usuario registrado con éxito✅", "\nNombre: " + registro.nombre() + "\nApellido: " + registro.apellido() + "\nEmail: " +  registro.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //NO VA
    @DeleteMapping("/eliminar-por-id/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) throws UserNotFoundException {
        usuarioService.eliminarPorId(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario eliminado con éxito✅");
    }

    @DeleteMapping("/eliminar-por-email/{email}")
    public ResponseEntity<String> eliminarUsuarioPorEmail(@PathVariable String email) throws UserNotFoundException {
        usuarioService.eliminarPorEmail(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario eliminado con éxito✅");
    }

    //CAMBIAR A OBTENER POR EMAIL
    @GetMapping("/obtener-por-id/{id}")
    public ResponseEntity<MostrarUsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id) throws UserNotFoundException {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + id + " no encontrado."));

        MostrarUsuarioDTO usuarioDTO = new MostrarUsuarioDTO(usuario);
        return ResponseEntity.ok()
                .body(usuarioDTO);
    }

    //metodo para que el usuario pueda modificar sus datos
    @PatchMapping("/modificar-datos") //CHEQUEADO
    public ResponseEntity<?> actualizarUsuario(@Valid @RequestBody ActualizarUsuarioDTO usuarioDTO) throws UserNotFoundException {
        usuarioService.actualizarUsuario(usuarioDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Modificaciones realizadas con éxito☑️");
        response.put("Modificaciones", usuarioDTO);
        return ResponseEntity.ok(response);
    }

    //metodo para modificar un usuario (desde admin)
    @PatchMapping("/modificar/{email}") //CHEQUEADO
    public ResponseEntity<String> actualizarUsuarioAdmin(@Valid @RequestBody ActualizarUsuarioDTO usuario, @PathVariable String email) throws UserNotFoundException {
        usuarioService.actualizarUsuarioAdmin(usuario, email);
        return ResponseEntity.ok("Modificaciones realizadas con éxito☑️\n" + usuario);
    }

    @PatchMapping("/modificar-password") //CHEQUEADO
    public ResponseEntity<String> modificarPassword(@Valid @RequestBody ActualizarPasswordDTO passwordDTO) throws UserNotFoundException {
        usuarioService.actualizarPassword(passwordDTO);
        return ResponseEntity.ok("Password modificado con éxito☑️");
    }

    //admin
    @PatchMapping("/actualizar-roles/{idUsuario}") //CHEQUEADO
    public ResponseEntity<Map<String, ActualizarRolesUsuarioDTO>> actualizarRoles(@PathVariable Long idUsuario, @Valid @RequestBody ActualizarRolesUsuarioDTO rolesDTO) throws UserNotFoundException {
        usuarioService.actualizarRolesUsuario(idUsuario, rolesDTO);
        Map<String, ActualizarRolesUsuarioDTO> response = new HashMap<>();
        response.put("Roles actualizados con éxito☑️", rolesDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/ver-perfil") //CHEQUEADO
    public ResponseEntity<Map<String, VerPerfilUsuarioDTO>> verPerfilUsuario() throws UserNotFoundException {
        Map<String, VerPerfilUsuarioDTO> response = new HashMap<>();
        VerPerfilUsuarioDTO usuarioDTO = usuarioService.verPerfilUsuario();
        response.put("Perfil:", usuarioDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ver-ciudades-disponibles") //CHEQUEADO
    public ResponseEntity<Map<String, List<String>>> verCiudadesDisponibles(){
        Map<String, List<String>> response = new HashMap<>();
        List<String> ciudadesDisponibles = CiudadesDisponibles.ciudadesDisponibles();
        response.put("Ciudades disponibles", ciudadesDisponibles);
        return ResponseEntity.ok(response);
    }

}
