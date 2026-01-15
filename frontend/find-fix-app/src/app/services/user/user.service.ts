import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { UpdatePasswordRequest, UpdateUserRequest, UserProfile, UserSearchFilters } from '../../models/user/user.model';
import { Observable, of, tap } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private apiUrlUser = 'http://localhost:8080/usuario';
  private apiUrlAdmin = 'http://localhost:8080/admin/usuarios';

  private http = inject(HttpClient);

  private userState = signal<UserProfile[]>([]);

  public users = this.userState.asReadonly();

  constructor() { }

  // ADMIN METHODS
  getUsers(forceReload: boolean = false): Observable<ApiResponse<UserProfile[]> | UserProfile[]> {
    if (!forceReload && this.userState().length > 0) {
      return of(this.userState());
    }

    return this.http.get<ApiResponse<UserProfile[]>>(`${this.apiUrlAdmin}`).pipe(
      tap({
        next: (response) => {
          this.userState.set(response.data);
          console.log('Usuarios cargados y guardados en el estado');
        },
        error: (error) => {
          console.error('Error al cargar usuarios:', error);
        }
      })
    )
  }

  desactivarUsuario(email: string): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrlAdmin}/desactivar/${email}`, {},
      { withCredentials: true });
  }

  activarUsuario(email: string): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(
      `${this.apiUrlAdmin}/activar/${email}`,
      {},
      { withCredentials: true }
    );
  }

    updateUserByAdmin(email: string, data: UpdateUserRequest): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(
      `${this.apiUrlAdmin}/modificar/${email}`,
      data,
      { withCredentials: true }
    );
  }

  filterUsers(filters: UserSearchFilters): Observable<ApiResponse<UserProfile[]>> {
    return this.http.post<ApiResponse<UserProfile[]>>(
      `${this.apiUrlAdmin}/filtrar`,
      filters,
      { withCredentials: true }
    );
  }
  
  // USER METHODS
  updateProfile(data: UpdateUserRequest): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrlUser}/modificar-datos`, data).pipe(
      tap(() => {
        console.log('Perfil actualizado correctamente');
      })
    );
  }

  updatePassword(data: UpdatePasswordRequest): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrlUser}/modificar-password`, data);
  }

  getProfile(): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.apiUrlUser}/ver-perfil`);
  }

  getCities(): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(
      `${this.apiUrlUser}/ciudades-disponibles`,
      { withCredentials: true }
    );
  }
}
