package com.findfix.find_fix_app.especialista.dto;


public record BuscarEspecialistaDTO(
            String oficio,
            String ciudad,
            Long dni,
            Long id,
            String email,
            Double minCalificacion
    ) {
        public boolean tieneOficio() { return oficio != null && !oficio.isEmpty(); }
        public boolean tieneCiudad() { return ciudad != null && !ciudad.isEmpty(); }
        public boolean tieneDni() { return dni != null; }
        public boolean tieneId() { return id != null; }
        public boolean tieneEmail() { return email != null && !email.isEmpty(); }
        public boolean tieneCalificacionMinima() { return minCalificacion != null; }
    }

