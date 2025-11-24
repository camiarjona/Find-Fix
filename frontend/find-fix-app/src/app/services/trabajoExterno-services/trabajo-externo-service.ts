import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { CrearTrabajoExternoDTO, TrabajoExternoDTO, ModificarTrabajoExternoDTO } from '../../models/trabajoExterno-models/trabajo-externo-model';

@Injectable({
  providedIn: 'root',
})
export class TrabajoExternoService {
  private apiUrl = 'http://localhost:8080/trabajos-externos';
  private http = inject(HttpClient);

  crearTrabajo(dto: CrearTrabajoExternoDTO): Observable<ApiResponse<TrabajoExternoDTO>> {
    return this.http.post<ApiResponse<TrabajoExternoDTO>>(`${this.apiUrl}/agregar`, dto, { withCredentials: true });
  }

  obtenerMisTrabajos(): Observable<ApiResponse<TrabajoExternoDTO[]>> {
    return this.http.get<ApiResponse<TrabajoExternoDTO[]>>(`${this.apiUrl}/mis-trabajos`, { withCredentials: true });
  }

  modificarTrabajo(tituloOriginal: string, dto: ModificarTrabajoExternoDTO): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/modificar/${tituloOriginal}`, dto, { withCredentials: true });
  }

  eliminarTrabajo(titulo: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/eliminar/${titulo}`, { withCredentials: true });
  }

  actualizarEstado(titulo: string, nuevoEstado: string): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/actualizar-estado/${titulo}/${nuevoEstado}`, {}, { withCredentials: true });
  }

  /// FALTARIA FILTRADOO
}
