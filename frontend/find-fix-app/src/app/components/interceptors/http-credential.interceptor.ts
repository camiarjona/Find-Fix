import { HttpInterceptorFn } from '@angular/common/http';

export const httpCredentialInterceptor: HttpInterceptorFn = (req, next) => {

    const esApiExterna = req.url.includes('apis.datos.gob.ar');

    if (esApiExterna) {
        return next(req);
    }

    const modifiedRequest = req.clone({
        withCredentials: true
    });

    return next(modifiedRequest);
};
