import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { CrearResenaDTO, MostrarResenaDTO } from '../../models/reseña/reseña.model';

@Injectable({
  providedIn: 'root',
})
export class ResenaService {

  private apiUrl = 'http://localhost:8080';
  private http = inject(HttpClient);
  private resenasUrl = `${this.apiUrl}/api/resenas`;

  crearResena(resena: CrearResenaDTO): Observable<ApiResponse<MostrarResenaDTO>> {
    return this.http.post<ApiResponse<MostrarResenaDTO>>(this.resenasUrl, resena);
  }

  /**
   * Obtiene todas las reseñas de un especialista (GET /api/resenas/especialista/{id}).
   */
  obtenerResenasPorEspecialista(especialistaId: number): Observable<ApiResponse<MostrarResenaDTO[]>> {
    const url = `${this.resenasUrl}/especialista/${especialistaId}`;
    return this.http.get<ApiResponse<MostrarResenaDTO[]>>(url);
  }

  /**
   * Obtiene todas las reseñas escritas por un cliente (GET /api/resenas/cliente/{id}).
   */
  obtenerResenasPorCliente(clienteId: number): Observable<ApiResponse<MostrarResenaDTO[]>> {
    const url = `${this.resenasUrl}/cliente/${clienteId}`;
    return this.http.get<ApiResponse<MostrarResenaDTO[]>>(url);
  }

  /**
   * Elimina una reseña por su ID (DELETE /api/resenas/{id}).
   */
  eliminarResena(resenaId: number): Observable<ApiResponse<void>> {
    const url = `${this.resenasUrl}/${resenaId}`;
    return this.http.delete<ApiResponse<void>>(url);
  }
}
