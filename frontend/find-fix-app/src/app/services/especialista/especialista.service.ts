import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ActualizarOficios, OficioEspecialista, PerfilEspecialista, ResenaEspecialista, SolicitudRecibida, TrabajoApp, TrabajoEspecialista, TrabajoExterno } from '../../models/especialista/especialista.model';
import { ApiResponse } from '../../models/api-response/apiResponse.model';

@Injectable({
  providedIn: 'root',
})
export class EspecialistaService {

  private apiURL = 'http://localhost:8080/especialistas';

  constructor(private http: HttpClient){ }

  /*Obtiene todas las solicitudes de trabajo del especialista*/

  getSolicitudesRecibidas(): Observable<SolicitudRecibida[]> {
    return this.http.get<ApiResponse<SolicitudRecibida[]>>(`${this.apiURL}/solicitud-trabajo/recibidas/mis-solicitudes`, { withCredentials: true })
      .pipe(map(response => response.data));
  }

  /*Obtiene todos los trabajos del especialista, trabajos de la app y externos en una sola lista*/

  getMisTrabajos(): Observable<TrabajoEspecialista[]> {
    return this.http.get<ApiResponse<{ [key: string]: any[] }>>(`${this.apiURL}/trabajos`, { withCredentials: true })
      .pipe(map(response => {
        const listaApp: TrabajoApp[] = response.data['Trabajos de la app'] || [];
        const listaExternos: TrabajoExterno[] = response.data['Trabajos externos'] || [];

        const trabajosAppNormalizados: TrabajoEspecialista[] = listaApp.map(t => ({
          id : t.id,
          titulo: t.titulo,
          estado: t.estado,
          fechaInicio: t.fechaInicio,
          fechaFin: t.fechaFin,
          nombreCliente: t.nombreCliente,
          descripcion: t.descripcion,
          presupuesto: t.presupuesto,
          tipo: 'APP'
        }));

        const trabajosExternosNormalizados: TrabajoEspecialista[] = listaExternos.map(t => ({
          id : t.id,
          titulo: t.titulo,
          estado: t.estado,
          fechaInicio: t.fechaInicio,
          fechaFin: t.fechaFin,
          nombreCliente: t.nombreCliente,
          descripcion: t.descripcion,
          presupuesto: t.presupuesto,
          tipo: 'EXTERNO'
        }));

        return [...trabajosAppNormalizados, ...trabajosExternosNormalizados];
      }));
  }


  /*Obtiene todas las reseñas recibidas del especialista */
  getMisResenas(): Observable<ResenaEspecialista[]> {
    return this.http.get<ApiResponse<ResenaEspecialista[]>>(`${this.apiURL}/resenas/recibidas`, { withCredentials: true })
      .pipe(map(res => res.data));
  }

  /** Obtiene el perfil completo del especialista*/
  getMiPerfil(): Observable<PerfilEspecialista> {
    return this.http.get<ApiResponse<PerfilEspecialista>>(`${this.apiURL}/ver-perfil`, { withCredentials: true })
      .pipe(map(res => res.data));
  }

  /** Actualizar los datos básicos del especialista*/
  actualizarDatos(datos: Partial<PerfilEspecialista>): Observable<any> {
    return this.http.patch(`${this.apiURL}/actualizar/mis-datos`, datos, { withCredentials: true });
  }

  /** Actualizar la lista de oficios y precios */
  actualizarOficios(oficios: ActualizarOficios): Observable<any> {
    return this.http.patch(`${this.apiURL}/actualizar/mis-oficios`, oficios, { withCredentials: true });
  }

  /** Responder a una solicitud (Aceptar o Rechazar) */
  responderSolicitud(id: number, estado: 'ACEPTADA' | 'RECHAZADA'): Observable<any> {
    return this.http.patch(`${this.apiURL}/solicitud-trabajo/actualizar-estado/${id}`, { estado }, { withCredentials: true });
  }
}
