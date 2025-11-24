import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { map, Observable } from 'rxjs';
import { EspecialistaDTO, FiltroEspecialistasDTO, CrearSolicitudTrabajoDTO } from '../../models/cliente/buscar-especialistas-models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PerfilEspecialista } from '../../models/especialista/especialista.model';

@Injectable({
  providedIn: 'root',
})
export class BuscarEspecialistaService {
  private apiUrl = 'http://localhost:8080';
  private http = inject(HttpClient);
  public especialistas: WritableSignal<EspecialistaDTO[]> = signal([]);
  public ciudades: WritableSignal<string[]> = signal([]);
  public oficios: WritableSignal<string[]> = signal([]);

  // 1. Obtener todos los especialistas disponibles
  obtenerEspecialistas(): void {
    this.http.get<ApiResponse<EspecialistaDTO[]>>(`${this.apiUrl}/especialistas/disponibles`)
      .subscribe({
        next: (res) => this.especialistas.set(res.data),
        error: (err) => console.error('Error cargando especialistas', err)
      });
  }

  // 2. Filtrar Especialistas
  filtrarEspecialistas(filtros: FiltroEspecialistasDTO): void {
    this.http.request<ApiResponse<EspecialistaDTO[]>>('POST', `${this.apiUrl}/especialistas/filtrar`, {
      body: filtros,
      headers: { 'Content-Type': 'application/json' }
    }).subscribe({
      next: (res) => this.especialistas.set(res.data),
      error: (err) => {
        console.error('Error filtrando', err);
        this.especialistas.set([]); // Si falla o no hay, vaciamos
      }
    });
  }

  // 3. Enviar Solicitud de Trabajo (Contratar)
  contratarEspecialista(dto: CrearSolicitudTrabajoDTO): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/solicitud-trabajo/registrar`, dto);
  }

  // 4. Cargar datos auxiliares para los selectores
  cargarDatosFiltros(): void {

    this.http.get<ApiResponse<string[]>>(`${this.apiUrl}/usuario/ciudades-disponibles`).subscribe(
      res => this.ciudades.set(res.data)
    );

    this.http.get<ApiResponse<string[]>>(`${this.apiUrl}/oficios/disponibles`).subscribe(
      res => this.oficios.set(res.data)
    );
  }

  obtenerPerfilCompleto(email: string): Observable<ApiResponse<PerfilEspecialista>> {
    const params = new HttpParams().set('email', email);
    return this.http.get<ApiResponse<PerfilEspecialista>>(`${this.apiUrl}/especialistas/detalle`, { params });
  }


 obtenerDisponiblesPublico(): Observable<ApiResponse<EspecialistaDTO[]>> {
    return this.http.get<ApiResponse<EspecialistaDTO[]>>(`${this.apiUrl}/especialistas/publico`);
  }
}
