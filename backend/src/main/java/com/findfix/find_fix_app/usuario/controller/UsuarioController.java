package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.enums.CiudadesDisponibles;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.usuario.dto.*;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<VerPerfilUsuarioDTO>> login(@Valid @RequestBody UsuarioLoginDTO loginDTO, HttpServletRequest request) throws UsuarioNotFoundException {
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        VerPerfilUsuarioDTO perfil = usuarioService.verPerfilUsuario();

        return ResponseEntity.ok(new ApiResponse<>("Login exitoso, bienvenido!", perfil));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        // Obtenemos la sesión actual y la invalidamos
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalida la sesión
        }
        // Limpiamos el contexto de seguridad
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(new ApiResponse<>("Has cerrado sesión exitosamente.", null));
    }

    //metodo para ver la lista de usuarios registrados
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MostrarUsuarioDTO>>> obtenerUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        return ResponseEntity.ok(new ApiResponse<>(
                "Lista de usuarios☑️",
                usuarios.stream().map(MostrarUsuarioDTO::new).toList()));
    }

    //metodo para que un usuario se registre en el sistema
    @PostMapping("/registrar") //CHEQUEADO
    public ResponseEntity<ApiResponse<VerPerfilUsuarioDTO>> registrarUsuario(@Valid @RequestBody RegistroDTO registro) throws UsuarioException, RolException {
        Usuario user = usuarioService.registrarNuevoUsuario(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Usuario registrado con éxito✅",
                new VerPerfilUsuarioDTO(user)));
    }

    //metodo para que el usuario pueda modificar sus datos
    @PatchMapping("/modificar-datos") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> actualizarUsuario(@Valid @RequestBody ActualizarUsuarioDTO usuarioDTO) throws UsuarioNotFoundException {
        usuarioService.actualizarUsuario(usuarioDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                "Modificaciones realizadas con éxito☑️", "Consulte su perfil para corroborar los cambios."));
    }

    //metodo para modificar un usuario (desde admin)
    @PatchMapping("/modificar/{email}") //CHEQUEADO
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> actualizarUsuarioAdmin(@Valid @RequestBody ActualizarUsuarioDTO usuario, @PathVariable String email) throws UsuarioNotFoundException {
        usuarioService.actualizarUsuarioAdmin(usuario, email);
        return ResponseEntity.ok(new ApiResponse<>("Modificaciones realizadas con éxito☑️", "Consulte la lista de usuarios para corroborar los cambios."));
    }

    @PatchMapping("/modificar-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")//CHEQUEADO
    public ResponseEntity<ApiResponse<String>> modificarPassword(@Valid @RequestBody ActualizarPasswordDTO passwordDTO) throws UsuarioNotFoundException {
        usuarioService.actualizarPassword(passwordDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                "Password modificado con éxito☑️", "{*****}"));
    }

    @GetMapping("/ver-perfil") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<VerPerfilUsuarioDTO>> verPerfilUsuario() throws UsuarioNotFoundException {
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
    public ResponseEntity<ApiResponse<List<MostrarUsuarioDTO>>> filtrarUsuarios(@RequestBody BuscarUsuarioDTO filtro) throws UsuarioNotFoundException, UsuarioException {
        List<Usuario> usuariosFiltrados = usuarioService.filtrarUsuarios(filtro);
        return ResponseEntity.ok(new ApiResponse<>("Coincidencias⬇️", usuariosFiltrados.stream().map(MostrarUsuarioDTO::new).toList()));
    }

}
