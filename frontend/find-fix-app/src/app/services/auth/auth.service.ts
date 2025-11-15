import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { LoginCredentials, UserProfile } from '../../models/user/user.model';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { tap } from 'rxjs';

type ActiveRole = 'cliente' | 'especialista' | 'admin';
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private apiUrl = 'http://localhost:8080'

  private http = inject(HttpClient);

  public activeRole = signal<ActiveRole | null>(null);
  public currentUser = signal<UserProfile | null>(null);

  public isLoggedIn = computed(() => this.currentUser() !== null);

  public canSwitchRoles = computed(() => {
    const user = this.currentUser();

    if (!user || !user.roles) return false;

    return user?.roles.includes('CLIENTE') && user.roles.includes('ESPECIALISTA');
  })

  login(credentials: LoginCredentials) {
    return this.http.post<ApiResponse<UserProfile>>(`${this.apiUrl}/usuario/login`, credentials)
      .pipe(
        tap(response => {
          this.currentUser.set(response.data);
        })
      );
  }

  logout() {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/usuario/logout`, {})
      .pipe(
        tap(() => {
          this.currentUser.set(null);
          this.activeRole.set(null);
        })
      );
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
