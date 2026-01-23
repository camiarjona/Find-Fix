import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { NotificacionModels } from '../../models/general/notificacion.models';

@Injectable({
  providedIn: 'root',
})
export class NotificacionService {
  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/notificaciones';

  constructor() { }

  obtenerMisNotificaciones(rol: string): Observable<ApiResponse<NotificacionModels[]>> {
    return this.http.get<ApiResponse<NotificacionModels[]>>(`${this.apiUrl}/mis-notificaciones?rolVista=${rol}`);
  }

  // PATCH: /notificaciones/{id}/leida
  marcarComoLeida(id: number): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/${id}/leida`, {});
  }
}
