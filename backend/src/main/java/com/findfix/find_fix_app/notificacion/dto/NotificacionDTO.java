package com.findfix.find_fix_app.notificacion.dto;
import java.time.LocalDateTime;
public record NotificacionDTO(
    Long id,
    String titulo,
    String mensaje,
    boolean leida,
    LocalDateTime fechaCreacion
) {}