import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { Observable } from 'rxjs';
import { ActualizarTrabajoApp, BuscarTrabajoApp, VisualizarTrabajoAppCliente, VisualizarTrabajoAppEspecialista } from '../../models/trabajoApp-models/trabajo-app-model';

@Injectable({
  providedIn: 'root',
})
export class TrabajoAppService {
  private apiUrl = 'http://localhost:8080/trabajos-app';
  private http = inject(HttpClient);


  /// Metodos para el Cliente
  obtenerTrabajosCliente(): Observable<ApiResponse<VisualizarTrabajoAppCliente[]>> {
    return this.http.get<ApiResponse<VisualizarTrabajoAppCliente[]>>(`${this.apiUrl}/cliente/mis-trabajos`, { withCredentials: true });
  }


  obtenerFichaCliente(id: number): Observable<ApiResponse<VisualizarTrabajoAppCliente>> {
    return this.http.get<ApiResponse<VisualizarTrabajoAppCliente>>(`${this.apiUrl}/cliente/ficha-trabajo/${id}`, { withCredentials: true });
  }

  filtrarTrabajosCliente(estado: string): Observable<ApiResponse<VisualizarTrabajoAppCliente[]>> {
    return this.http.post<ApiResponse<VisualizarTrabajoAppCliente[]>>(
      `${this.apiUrl}/cliente/filtrar/${estado}`,
      {},
      { withCredentials: true }
    );
  }

  /// Metodos para el especialista
  obtenerTrabajosEspecialista(): Observable<ApiResponse<VisualizarTrabajoAppEspecialista[]>> {
    return this.http.get<ApiResponse<VisualizarTrabajoAppEspecialista[]>>(`${this.apiUrl}/especialista/mis-trabajos`, { withCredentials: true });
  }


  obtenerFichaEspecialista(tituloBuscado: string): Observable<ApiResponse<VisualizarTrabajoAppEspecialista>> {
    return this.http.get<ApiResponse<VisualizarTrabajoAppEspecialista>>(`${this.apiUrl}/especialista/ficha-trabajo/${tituloBuscado}`, { withCredentials: true });
  }


  actualizarDatosTrabajo(tituloBuscado: string, datos: ActualizarTrabajoApp): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/actualizar-datos/${tituloBuscado}`, datos, { withCredentials: true });
  }


  actualizarEstadoTrabajo(titulo: string, nuevoEstado: string): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/actualizar-estado/${titulo}/${nuevoEstado}`, {}, { withCredentials: true });
  }

  /// FALTARIA FILTRADOO

}
