package com.findfix.find_fix_app.usuario.controller.delete;

import com.findfix.find_fix_app.usuario.service.delete.DeleteService;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DeleteController {
    private final DeleteService deleteService;

    @DeleteMapping("/eliminar/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> eliminarUsuario(@PathVariable String email) throws UsuarioNotFoundException, RolNotFoundException, EspecialistaNotFoundException {
        deleteService.eliminarCuentaPorEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Usuario eliminado con éxito✅", "{}"));
    }

}
