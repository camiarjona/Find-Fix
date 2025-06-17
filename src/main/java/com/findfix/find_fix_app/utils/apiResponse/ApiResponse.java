package com.findfix.find_fix_app.utils.apiResponse;

public record ApiResponse<T>(String mensaje, T data) {}
