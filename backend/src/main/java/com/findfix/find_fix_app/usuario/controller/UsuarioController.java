package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.enums.CiudadesDisponibles;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.usuario.dto.*;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    //metodo para que el usuario pueda modificar sus datos
    @PatchMapping("/modificar-datos") //CHEQUEADO
    @PreAuthorize("hasAnyRole('CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> actualizarUsuario(@Valid @RequestBody ActualizarUsuarioDTO usuarioDTO) throws UsuarioNotFoundException {
        usuarioService.actualizarUsuario(usuarioDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                "Modificaciones realizadas con éxito☑️", "Consulte su perfil para corroborar los cambios."));
    }

    @PatchMapping("/modificar-password")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ESPECIALISTA')")//CHEQUEADO
    public ResponseEntity<ApiResponse<String>> modificarPassword(@Valid @RequestBody ActualizarPasswordDTO passwordDTO) throws UsuarioNotFoundException {
        usuarioService.actualizarPassword(passwordDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                "Password modificado con éxito☑️", "{*****}"));
    }

    @GetMapping("/ver-perfil") //CHEQUEADO
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE', 'ESPECIALISTA')")
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
}
