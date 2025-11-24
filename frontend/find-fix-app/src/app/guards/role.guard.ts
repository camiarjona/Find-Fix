import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { UserService } from '../services/user/user.service';
import { map, catchError, of, switchMap } from 'rxjs';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const userService = inject(UserService);
  const router = inject(Router);

  const expectedRole = route.data['role'];

  // Función auxiliar para verificar el rol una vez tenemos el usuario
  const checkRole = (user: any) => {
    const hasRole = user.roles.includes(expectedRole) || user.roles.includes(expectedRole);
    if (hasRole) {
      return true;
    }
    alert('No tienes permisos para acceder a esta sección.');
    router.navigate(['/']);
    return false;
  };

  if (authService.currentUser()) {
    return checkRole(authService.currentUser());
  }

  return userService.getProfile().pipe(
    map((response) => {
      if (response.data) {
        authService.currentUser.set(response.data);
        return checkRole(response.data);
      }
      router.navigate(['/auth']);
      return false;
    }),
    catchError(() => {
      router.navigate(['/auth']);
      return of(false);
    })
  );
};
