// frontend/find-fix-app/src/app/interceptors/http-credential.interceptor.ts

import { HttpInterceptorFn } from '@angular/common/http';

// Usamos HttpInterceptorFn para tipar nuestra función.
export const httpCredentialInterceptor: HttpInterceptorFn = (req, next) => {
    // 1. Clona la solicitud para añadir la opción withCredentials: true
    const modifiedRequest = req.clone({
        withCredentials: true
    });

    // 2. Continúa la ejecución con la solicitud modificada
    return next(modifiedRequest);
};
