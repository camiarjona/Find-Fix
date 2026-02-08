import { Injectable, signal, WritableSignal } from '@angular/core';
import { OficioModel } from '../../models/admin-models/oficio-model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { Observable, tap, catchError, throwError, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OficiosService {
  private apiUrl = 'http://localhost:8080/oficios';
  private apiUrlAdmin = 'http://localhost:8080/admin/oficios';
  private oficiosState = signal<OficioModel[]>([]);
  oficios = this.oficiosState.asReadonly();
  public formStatus: WritableSignal<'hidden' | 'creating' | 'editing'> = signal('hidden');

  public selectedOficio: WritableSignal<OficioModel | null> = signal(null);

  constructor(private http: HttpClient) {
  }

  getOficios(forceReload: boolean = false): Observable<ApiResponse<OficioModel[]> | OficioModel[]> {
    if (!forceReload && this.oficiosState().length > 0) {
      return of(this.oficiosState());
    }

    return this.http.get<ApiResponse<OficioModel[]>>(this.apiUrl).pipe(
      tap({
        next: (response) => {
          this.oficiosState.set(response.data);
          console.log('Oficios cargados y guardados en el estado');
        },
        error: (error) => {
          console.error('Error al cargar oficios:', error);
        }
      })
    )
  }

  deleteOficio(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrlAdmin}/eliminar/${id}`).pipe(
      tap(() => {
        this.oficiosState.update(currentOficios =>
          currentOficios.filter(oficio => oficio.id !== id)
        );
      })
    );
  }

  addOficio(nombreOficio: string): Observable<ApiResponse<string>> {
    const newOficioPayload = { nombre: nombreOficio.toUpperCase() };

    return this.http.post<ApiResponse<string>>(`${this.apiUrlAdmin}/agregar`, newOficioPayload)
      .pipe(
        tap(() => {
          this.formStatus.set('hidden');
          this.getOficios(true).subscribe();
        }),
        catchError((err: HttpErrorResponse) => {
          console.error("Error en addOficio:", err.error?.mensaje || err.message);
          return throwError(() => err);
        })
      );
  }

  updateOficio(id: number, nombreOficio: string): Observable<ApiResponse<string>> {
    const payload = { nombre: nombreOficio.toUpperCase() };

    return this.http.patch<ApiResponse<string>>(`${this.apiUrlAdmin}/actualizar/${id}`, payload)
      .pipe(
        tap(() => {
          this.oficiosState.update(current =>
            current.map(oficio =>
              oficio.id === id ? { ...oficio, nombre: nombreOficio.toUpperCase() } : oficio
            )
          );

          this.formStatus.set('hidden');
          this.selectedOficio.set(null);
        }),
        catchError((err: HttpErrorResponse) => {
          console.error("Error en updateOficio:", err.error?.mensaje || err.message);
          return throwError(() => err);
        })
      );
  }
}
