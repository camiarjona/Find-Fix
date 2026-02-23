package com.findfix.find_fix_app.auth.service;

import org.springframework.stereotype.Service;

@Service
public interface PasswordResetService {
    String solicitarRecuperacionPassword(String email);
    String restablecerPassword(String token, String nuevaPassword);
}
