import { Router } from '@angular/router';
import { HttpClient} from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { LoginCredentials, RegisterCredentials, UserProfile } from '../../models/user/user.model';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { catchError, Observable, of, tap } from 'rxjs';

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
    return this.http.post<ApiResponse<UserProfile>>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          this.currentUser.set(response.data);
        })
      );
  }

  logout(): void {
    console.log('🚪 Iniciando proceso de logout...');

    this.http.post(`${this.apiUrl}/auth/logout`, {}, { responseType: 'text' })
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
    this.currentUser.set(null);
    this.activeRole.set(null);
    this.router.navigate(['/auth']); // O al home
  }

  register(register: RegisterCredentials): Observable<ApiResponse<UserProfile>> {
    return this.http.post<ApiResponse<UserProfile>>(`${this.apiUrl}/auth/registrar`, register);
  }

  public setInitialRole(role: ActiveRole) {
      this.activeRole.set(role);
  }

  public switchActiveRole() {
    if (!this.canSwitchRoles()) return;

    const newRole = this.activeRole() === 'cliente' ? 'especialista' : 'cliente';
    this.activeRole.set(newRole);
  }

}
