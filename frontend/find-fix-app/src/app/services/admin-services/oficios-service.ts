import { Injectable, signal, WritableSignal } from '@angular/core';
import { OficioModel } from '../../models/admin-models/oficio-model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { Observable, tap, catchError, throwError, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OficiosService {
  private apiUrl = '/oficios';
  private oficiosState = signal<OficioModel[]>([]);
  oficios = this.oficiosState.asReadonly();
  public formStatus: WritableSignal<'hidden' | 'creating' | 'editing'> = signal('hidden');

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
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/eliminar/${id}`).pipe(
      tap(() => {
        this.oficiosState.update(currentOficios =>
          currentOficios.filter(oficio => oficio.id !== id)
        );
      })
    );
  }

  addOficio(nombreOficio: string): Observable<ApiResponse<string>> {
    const newOficioPayload = { nombre: nombreOficio.toUpperCase() };

    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/agregar`, newOficioPayload)
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
}
