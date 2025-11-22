import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { MostrarSolicitud } from '../../models/cliente/solicitud-especialista.model';
import { ActualizarSolicitudDTO, FichaCompletaSolicitud } from '../../models/cliente/solicitud-especialista.model';

@Injectable({
  providedIn: 'root',
})
export class SolicitudEspecialistaAdminService {

  private apiUrl = 'http://localhost:8080';
  private http = inject(HttpClient);

  // Endpoint de Solicitudes de Especialista (lo que tu backend mapea a SolicitudEspecialistaController)
  private solicitudesUrl = `${this.apiUrl}/solicitud-especialista`;

  /**
   * Obtiene la lista de solicitudes de especialista para el administrador.
   * La petición va a: http://localhost:8080/solicitudEspecialista/admin/listado
   */
  getSolicitudesAdmin(): Observable<ApiResponse<MostrarSolicitud[]>> {
  return this.http.get<ApiResponse<MostrarSolicitud[]>>(`${this.solicitudesUrl}`);
}

  /**
   * Obtiene la ficha completa de una solicitud.
   */
  getFichaCompleta(id: number): Observable<ApiResponse<MostrarSolicitud>> {
    return this.http.get<ApiResponse<MostrarSolicitud>>(`${this.solicitudesUrl}/ficha/${id}`);
  }

  actualizarEstado(id: number, dto: ActualizarSolicitudDTO): Observable<ApiResponse<FichaCompletaSolicitud>> {
    const url = `${this.solicitudesUrl}/actualizar/${id}`; // URL: .../solicitud-especialista/actualizar/{id}

    return this.http.patch<ApiResponse<FichaCompletaSolicitud>>(url, dto);
  }

}
