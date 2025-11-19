import { Injectable, signal, WritableSignal } from '@angular/core';
import { RolModel } from '../../models/admin-models/rol-model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { catchError, Observable, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RolesService {
  private apiURL = '/roles';
  private rolesState = signal<RolModel[]>([])
  public formStatus: WritableSignal<'hidden' | 'creating'> = signal('hidden');
   roles = this.rolesState.asReadonly();

  constructor(private http : HttpClient)
  {
    /// metodo get de roles
    this.getRoles();
  }
  getRoles()
  {
    this.http.get<ApiResponse<RolModel[]>>(this.apiURL).subscribe({
      next: (response) => {
        this.rolesState.set(response.data);
        console.log('Roles cargados con éxito:', response.data);
      },
      error: (err) => {
        console.error('Error al cargar roles (Código HTTP:', err.status, ')', err);
      }
    });
  }

  deleteRol(nombre: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.apiURL}/${nombre}`).pipe(
      tap(() => {
        this.rolesState.update(roles => roles.filter(r => r.nombre !== nombre));
      })
    );
  }

  addRol(nombreRol: string): Observable<ApiResponse<string>> {
    const newRol = { nombre: nombreRol.toUpperCase() };
    return this.http.post<ApiResponse<string>>(this.apiURL, newRol).pipe(
      tap(() => {
        this.formStatus.set('hidden');
        this.getRoles();
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => err);
      })
    );
  }

}
