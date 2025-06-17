package com.findfix.find_fix_app.utils.apiResponse;

import lombok.Getter;

@Getter
public class ApiResponse <T> {

    private final String mensaje;
    private final T data;

    public ApiResponse(String mensaje, T data) {
        this.mensaje = mensaje;
        this.data = data;
    }
}
