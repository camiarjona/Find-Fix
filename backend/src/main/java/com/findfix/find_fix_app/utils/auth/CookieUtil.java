package com.findfix.find_fix_app.utils.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${jwt.refresh-expiration-ms}")
    private int refreshDurationMs;

    @Value("${jwt.cookie.secure:false}")
    private boolean isCookieSecure;

    public ResponseCookie crearCookieRefresh(String token) {
        int durationSeconds = refreshDurationMs / 1000;

        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(durationSeconds)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie limpiarCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}