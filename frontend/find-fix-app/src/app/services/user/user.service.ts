import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { UpdatePasswordRequest, UpdateUserRequest, UserProfile } from '../../models/user/user.model';
import { Observable, of, tap } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private apiUrl = 'http://localhost:8080';

  private http = inject(HttpClient);

  private userState = signal<UserProfile[]>([]);

  public users = this.userState.asReadonly();

  constructor() { }

  getUsers(forceReload: boolean = false): Observable<ApiResponse<UserProfile[]> | UserProfile[]> {
    if (!forceReload && this.userState().length > 0) {
      return of(this.userState());
    }

    return this.http.get<ApiResponse<UserProfile[]>>(`${this.apiUrl}/usuario`).pipe(
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

  deleteUser(email: string) {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/admin/eliminar/${email}`).pipe(
      tap(() => {
        this.userState.update(currentUsers =>
          currentUsers.filter(user => user.email !== email)
        );
      })
    )
  }

  updateProfile(data: UpdateUserRequest): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/usuario/modificar-datos`, data).pipe(
      tap(() => {
        console.log('Perfil actualizado correctamente');
      })
    );
  }

  updatePassword(data: UpdatePasswordRequest): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/usuario/modificar-password`, data);
  }

  getProfile(): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.apiUrl}/usuario/ver-perfil`);
  }

  getCities(): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(
      `${this.apiUrl}/usuario/ciudades-disponibles`,
      { withCredentials: true }
    );
  }
}
