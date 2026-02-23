package com.findfix.find_fix_app.auth.controller;

import com.findfix.find_fix_app.auth.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/solicitar-recuperacion")
    public ResponseEntity<String> solicitarRecuperacion(@RequestParam("email") String email) {
        String resultado = passwordResetService.solicitarRecuperacionPassword(email);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/restablecer-password")
    public ResponseEntity<String> restablecerPassword(
            @RequestParam("token") String token,
            @RequestBody Map<String, String> body) {

        String nuevaPassword = body.get("nuevaPassword");

        String resultado = passwordResetService.restablecerPassword(token, nuevaPassword);
        return ResponseEntity.ok(resultado);
    }
}