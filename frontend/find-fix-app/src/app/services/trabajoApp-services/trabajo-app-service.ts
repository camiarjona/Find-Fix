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
    return this.http.get<ApiResponse<VisualizarTrabajoAppCliente[]>>(`${this.apiUrl}/cliente/mis-trabajos`);
  }


  obtenerFichaCliente(id: number): Observable<ApiResponse<VisualizarTrabajoAppCliente>> {
    return this.http.get<ApiResponse<VisualizarTrabajoAppCliente>>(`${this.apiUrl}/cliente/ficha-trabajo/${id}`);
  }

  filtrarTrabajosCliente(estado: string): Observable<ApiResponse<VisualizarTrabajoAppCliente[]>> {
    return this.http.get<ApiResponse<VisualizarTrabajoAppCliente[]>>(`${this.apiUrl}/cliente/filtrar/${estado}`);
  }

 /// Metodos para el especialista
  obtenerTrabajosEspecialista(): Observable<ApiResponse<VisualizarTrabajoAppEspecialista[]>> {
    return this.http.get<ApiResponse<VisualizarTrabajoAppEspecialista[]>>(`${this.apiUrl}/especialista/mis-trabajos`);
  }


  obtenerFichaEspecialista(tituloBuscado: string): Observable<ApiResponse<VisualizarTrabajoAppEspecialista>> {
    return this.http.get<ApiResponse<VisualizarTrabajoAppEspecialista>>(`${this.apiUrl}/especialista/ficha-trabajo/${tituloBuscado}`);
  }


  actualizarDatosTrabajo(tituloBuscado: string, datos: ActualizarTrabajoApp): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/actualizar-datos/${tituloBuscado}`, datos);
  }


  actualizarEstadoTrabajo(titulo: string, nuevoEstado: string): Observable<ApiResponse<string>> {
    return this.http.patch<ApiResponse<string>>(`${this.apiUrl}/actualizar-estado/${titulo}/${nuevoEstado}`, {});
  }

  /// FALTARIA FILTRADOO

}
