package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.enums.CiudadesDisponibles;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
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
import java.util.stream.Stream;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {
    private final UsuarioService usuarioService;

    //metodo para ver la lista de usuarios registrados
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MostrarUsuarioDTO>>> obtenerUsuarios() throws UserException {
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        return ResponseEntity.ok(new ApiResponse<>(
                "Lista de usuarios encontrada️☑️",
                usuarios.stream().map(MostrarUsuarioDTO::new).toList()));
    }

    //metodo para que un usuario se registre en el sistema
    @PostMapping("/registrar") //CHEQUEADO
    public ResponseEntity<ApiResponse<MostrarRegistroDTO>> registrarUsuario(@Valid @RequestBody RegistroDTO registro) throws UserException, RolException {
        usuarioService.registrarNuevoUsuario(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Usuario registrado con éxito✅",
                new MostrarRegistroDTO(registro)));
    }

    //metodo para eliminar un usuario por su email
    @DeleteMapping("/eliminar/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> eliminarUsuarioPorEmail(@PathVariable String email) throws UserNotFoundException {
        usuarioService.eliminarPorEmail(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(
                "Usuario eliminado con éxito✅", "{}"));
    }

    //metodo para que el usuario pueda modificar sus datos
    @PatchMapping("/modificar-datos") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> actualizarUsuario(@Valid @RequestBody ActualizarUsuarioDTO usuarioDTO) throws UserNotFoundException {
        usuarioService.actualizarUsuario(usuarioDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                "Modificaciones realizadas con éxito☑️", "Consulte su perfil para corroborar los cambios."));
    }

    //metodo para modificar un usuario (desde admin)
    @PatchMapping("/modificar/{email}") //CHEQUEADO
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> actualizarUsuarioAdmin(@Valid @RequestBody ActualizarUsuarioDTO usuario, @PathVariable String email) throws UserNotFoundException {
        usuarioService.actualizarUsuarioAdmin(usuario, email);
        return ResponseEntity.ok(new ApiResponse<>("Modificaciones realizadas con éxito☑️", "Consulte la lista de usuarios para corroborar los cambios."));
    }

    @PatchMapping("/modificar-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")//CHEQUEADO
    public ResponseEntity<ApiResponse<String>> modificarPassword(@Valid @RequestBody ActualizarPasswordDTO passwordDTO) throws UserNotFoundException {
        usuarioService.actualizarPassword(passwordDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                "Password modificado con éxito☑️", "{*****}"));
    }

    //metodo para actualizar los roles de un usuario
    @PatchMapping("/actualizar-roles/{email}") //CHEQUEADO
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ActualizarRolesUsuarioDTO>> actualizarRoles(@PathVariable String email, @Valid @RequestBody ActualizarRolesUsuarioDTO rolesDTO) throws UserNotFoundException {
        usuarioService.actualizarRolesUsuario(email, rolesDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Roles actualizados con éxito☑️", rolesDTO));
    }

    @GetMapping("/ver-perfil") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<VerPerfilUsuarioDTO>> verPerfilUsuario() throws UserNotFoundException {
        Map<String, VerPerfilUsuarioDTO> response = new HashMap<>();
        VerPerfilUsuarioDTO usuarioDTO = usuarioService.verPerfilUsuario();
        return ResponseEntity.ok(new ApiResponse<>("\uD83D\uDC64Perfil\uD83D\uDC64", usuarioDTO));
    }

    @GetMapping("/ciudades-disponibles") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<String>>> verCiudadesDisponibles() {
        List<String> ciudadesDisponibles = CiudadesDisponibles.ciudadesDisponibles();
        return ResponseEntity.ok(new ApiResponse<>("Ciudades disponibles", ciudadesDisponibles));
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasRole('ADMIN')") //CHEQUEADO
    public ResponseEntity<ApiResponse<Stream<MostrarUsuarioDTO>>> filtrarUsuarios(@RequestBody BuscarUsuarioDTO filtro) throws UserNotFoundException, UserException {
        List<Usuario> usuariosFiltrados = usuarioService.filtrarUsuarios(filtro);
        return ResponseEntity.ok(new ApiResponse<>("Coincidencias⬇️", usuariosFiltrados.stream().map(MostrarUsuarioDTO::new)));
    }

}
