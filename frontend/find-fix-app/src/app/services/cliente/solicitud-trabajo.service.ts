import { Injectable } from '@angular/core';
import { BuscarSolicitudDTO, MostrarSolicitudTrabajoDTO } from '../../models/cliente/solicitud-trabajo.models';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class SolicitudTrabajoService {
  private apiURL = 'http://localhost:8080/solicitud-trabajo';

  constructor(private http: HttpClient) {}

  obtenerMisSolicitudesEnviadas(): Observable<ApiResponse<MostrarSolicitudTrabajoDTO[]>> {
    return this.http.get<ApiResponse<MostrarSolicitudTrabajoDTO[]>>(
      `${this.apiURL}/enviadas/mis-solicitudes`,
      { withCredentials: true }
    );
  }


  obtenerSolicitudPorId(id: number): Observable<ApiResponse<MostrarSolicitudTrabajoDTO>> {
    return this.http.get<ApiResponse<MostrarSolicitudTrabajoDTO>>(
      `${this.apiURL}/${id}`,
      { withCredentials: true }
    );
  }


  eliminarSolicitud(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(
      `${this.apiURL}/eliminar/${id}`,
      { withCredentials: true }
    );
  }

  filtrarSolicitudesEnviadas(filtro: BuscarSolicitudDTO): Observable<ApiResponse<MostrarSolicitudTrabajoDTO[]>> {
    return this.http.request<ApiResponse<MostrarSolicitudTrabajoDTO[]>>(
      'GET',
      `${this.apiURL}/filtrar/enviadas`,
      {
        body: filtro,
        withCredentials: true
      }
    );
  }

}
