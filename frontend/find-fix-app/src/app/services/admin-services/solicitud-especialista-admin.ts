import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../../models/api-response/apiResponse.model';
import { MostrarSolicitud } from '../../models/cliente/solicitud-especialista.model';
import { ActualizarSolicitudDTO, FichaCompletaSolicitud } from '../../models/cliente/solicitud-especialista.model';

@Injectable({
  providedIn: 'root',
})
export class SolicitudEspecialistaAdminService {

  private apiUrl = 'http://localhost:8080';
  private http = inject(HttpClient);
  private solicitudesUrl = `${this.apiUrl}/solicitud-especialista`;

  obtenerSolicitudesEspecialista(page: number, size: number): Observable<PageResponse<MostrarSolicitud>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<MostrarSolicitud>>(
      `${this.solicitudesUrl}`,
      { params }
    );
  }

  getFichaCompleta(id: number): Observable<ApiResponse<FichaCompletaSolicitud>> {
    return this.http.get<ApiResponse<FichaCompletaSolicitud>>(`${this.solicitudesUrl}/ficha/${id}`);
  }

  actualizarEstado(id: number, dto: ActualizarSolicitudDTO): Observable<ApiResponse<FichaCompletaSolicitud>> {
    const url = `${this.solicitudesUrl}/actualizar/${id}`;

    return this.http.patch<ApiResponse<FichaCompletaSolicitud>>(url, dto);
  }

}
