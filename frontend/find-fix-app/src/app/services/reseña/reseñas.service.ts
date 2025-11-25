import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model';

import {
  CrearResenaDTO,
  MostrarResenaClienteDTO,
  MostrarResenaEspecialistaDTO,
  MostrarResenaDTO
} from '../../models/reseña/reseña.model';

@Injectable({
  providedIn: 'root',
})
export class ResenaService {

  private apiUrl = 'http://localhost:8080';
  private http = inject(HttpClient);
  private resenasUrl = `${this.apiUrl}/resenas`;

  crearResena(dto: CrearResenaDTO): Observable<ApiResponse<MostrarResenaClienteDTO>> {
    return this.http.post<ApiResponse<MostrarResenaClienteDTO>>(`${this.resenasUrl}/registrar`, dto);
  }

  buscarResenaPorId(id: number): Observable<ApiResponse<MostrarResenaClienteDTO>> {
    return this.http.get<ApiResponse<MostrarResenaClienteDTO>>(`${this.resenasUrl}/buscar/${id}`);
  }

  buscarResenaPorTituloTrabajo(titulo: string): Observable<ApiResponse<MostrarResenaDTO>> {
    return this.http.get<ApiResponse<MostrarResenaDTO>>(`${this.resenasUrl}/trabajo/${titulo}`);
  }

  obtenerResenasRecibidas(): Observable<ApiResponse<MostrarResenaEspecialistaDTO[]>> {
    return this.http.get<ApiResponse<MostrarResenaEspecialistaDTO[]>>(`${this.resenasUrl}/recibidas`);
  }

  obtenerResenasEnviadas(): Observable<ApiResponse<MostrarResenaClienteDTO[]>> {
    return this.http.get<ApiResponse<MostrarResenaClienteDTO[]>>(`${this.resenasUrl}/enviadas`);
  }

  eliminarResena(id: number): Observable<ApiResponse<string>> {
    // El backend devuelve ApiResponse<String>, por lo que usamos string como tipo de dato en ApiResponse
    return this.http.delete<ApiResponse<string>>(`${this.resenasUrl}/eliminar/${id}`);
  }
}
