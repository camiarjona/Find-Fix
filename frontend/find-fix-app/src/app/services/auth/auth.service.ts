import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
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

  constructor() {
    this.recuperarSesion();
  }


  login(credentials: LoginCredentials) {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/auth/login`, credentials, { withCredentials: true })
      .pipe(
        tap(response => {
          if (response.data) {
            this.handleAuthentication(response.data);
          }
        })
      );
  }

  loginWithGoogle(googleToken: string) {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/auth/login/google`, { token: googleToken }, { withCredentials: true })
      .pipe(
        tap(response => {
          if (response.data) {
            this.handleAuthentication(response.data);
          }
        }
        ));
  }

  private handleAuthentication(data: AuthResponse) {
    // 1. Guardar Access Token
    localStorage.setItem('accessToken', data.accessToken);

    // 2. Mapear respuesta a UserProfile
    const user: UserProfile = {
      usuarioId: data.id,
      email: data.email,
      nombre: data.nombre,
      apellido: data.apellido,
      roles: data.roles,
      activo: data.activo
    };

    localStorage.setItem('currentUser', JSON.stringify(user));

    this.currentUser.set(user);
  }

  private recuperarSesion() {
    const accessToken = localStorage.getItem('accessToken');
    const userStored = localStorage.getItem('currentUser');

    if (accessToken && userStored) {
      try {
        const user: UserProfile = JSON.parse(userStored);
        this.currentUser.set(user);
      } catch (e) {
        console.error('Error recuperando sesión local', e);
        this.logout();
      }
    }
  }

  logout(): void {
    console.log('Iniciando proceso de logout...');

    this.http.post(`${this.apiUrl}/auth/logout`, {}, { responseType: 'text', withCredentials: true })
      .subscribe({
        next: () => {
          console.log('Backend confirmó logout. Cookie eliminada.');
          this.finalizarSesionLocal();
        },
        error: (err) => {
          console.warn('El backend no respondió OK, pero cerramos sesión igual.', err);
          this.finalizarSesionLocal();
        }
      });
  }

  private finalizarSesionLocal() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('currentUser');
    this.currentUser.set(null);
    this.activeRole.set(null);
    this.router.navigate(['/auth']);
  }

  register(register: RegisterCredentials): Observable<ApiResponse<UserProfile>> {
    return this.http.post<ApiResponse<UserProfile>>(`${this.apiUrl}/auth/registrar`, register);
  }

  reenviarCorreoActivacion(email: string) {
    return this.http.post(`${this.apiUrl}/auth/reenviar-token`, null, {
      params: { email: email },
      responseType: 'text'
    });
  }

  confirmarCuenta(token: string) {
    return this.http.get(`${this.apiUrl}/auth/confirmar-cuenta`, {
      params: { token: token },
      responseType: 'text'
    });
  }

  refreshToken(): Observable<ApiResponse<{ accessToken: string }>> {
    return this.http.post<ApiResponse<{ accessToken: string }>>(
      `${this.apiUrl}/auth/refresh-token`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(response => {
        if (response.data?.accessToken) {
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
