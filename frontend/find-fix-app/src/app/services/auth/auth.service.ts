import { Router } from '@angular/router';
import { HttpClient} from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { LoginCredentials, RegisterCredentials, UserProfile } from '../../models/user/user.model';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { catchError, Observable, of, tap } from 'rxjs';
import { AuthResponse } from '../../models/auth/auth-response.model';

type ActiveRole = 'cliente' | 'especialista' | 'admin';
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private apiUrl = 'http://localhost:8080'

  private http = inject(HttpClient);
  private router = inject(Router);

  public activeRole = signal<ActiveRole | null>(null);
  public currentUser = signal<UserProfile | null>(null);

  public isLoggedIn = computed(() => this.currentUser() !== null);

  public canSwitchRoles = computed(() => {
    const user = this.currentUser();

    if (!user || !user.roles) return false;

    return user?.roles.includes('CLIENTE') && user.roles.includes('ESPECIALISTA');
  })


  login(credentials: LoginCredentials) {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/auth/login`, credentials, {withCredentials: true})
      .pipe(
        tap(response => {
          if (response.data) {
            localStorage.setItem('accessToken', response.data.accessToken);

            const user: UserProfile = {
              email: response.data.email,
              nombre: response.data.nombre,
              apellido: response.data.apellido,
              roles: response.data.roles,
              activo: response.data.activo
            };

            this.currentUser.set(user);
          }
        })
      );
  }

  logout(): void {
    console.log('🚪 Iniciando proceso de logout...');

    this.http.post(`${this.apiUrl}/auth/logout`, {}, { responseType: 'text', withCredentials: true})
      .subscribe({
        next: () => {
          console.log('✅ Backend confirmó logout. Cookie eliminada.');
          this.finalizarSesionLocal();
        },
        error: (err) => {
          console.warn('⚠️ El backend no respondió OK, pero cerramos sesión igual.', err);
          // Forzamos el cierre local aunque el backend falle o dé error de red
          this.finalizarSesionLocal();
        }
      });
  }

  private finalizarSesionLocal() {
    localStorage.removeItem('accessToken');
    this.currentUser.set(null);
    this.activeRole.set(null);
    this.router.navigate(['/auth']); // O al home
  }

  register(register: RegisterCredentials): Observable<ApiResponse<UserProfile>> {
    return this.http.post<ApiResponse<UserProfile>>(`${this.apiUrl}/auth/registrar`, register);
  }

  refreshToken(): Observable<ApiResponse<{ accessToken: string }>> {
    return this.http.post<ApiResponse<{ accessToken: string }>>(
      `${this.apiUrl}/auth/refresh-token`,
      {},
      { withCredentials: true } // Envía la cookie automáticamente
    ).pipe(
      tap(response => {
        if(response.data?.accessToken) {
          localStorage.setItem('accessToken', response.data.accessToken);
        }
      })
    );
  }

  // --- UTILIDADES ---

  public setInitialRole(role: ActiveRole) {
      this.activeRole.set(role);
  }

  public switchActiveRole() {
    if (!this.canSwitchRoles()) return;

    const newRole = this.activeRole() === 'cliente' ? 'especialista' : 'cliente';
    this.activeRole.set(newRole);
  }

  getToken(): string | null {
      return localStorage.getItem('accessToken');
  }

}
