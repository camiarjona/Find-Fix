import { HttpClient } from '@angular/common/http';
import { Injectable, TRANSLATIONS } from '@angular/core';
import { FichaCompletaSolicitud, MandarSolicitud, MostrarSolicitud } from '../../models/cliente/solicitud-especialista.model';
import { Observable, of } from 'rxjs';
import { ApiResponse } from '../../models/apiResponse.model';

@Injectable({
  providedIn: 'root',
})
export class SolicitudEspecialistaService {
    private apiURL = 'http://localhost:8080/solicitud-especialista'

    constructor(private http : HttpClient){}

    enviarSolicitud(dto : MandarSolicitud) : Observable<ApiResponse<MandarSolicitud>>{
      return this.http.post<ApiResponse<MandarSolicitud>>(`${this.apiURL}/enviar`, dto, { withCredentials: true })

    }

    obtenerMisSolicitudes() : Observable<ApiResponse<MostrarSolicitud[]>>{
     return this.http.get<ApiResponse<MostrarSolicitud[]>>(`${this.apiURL}/mis-solicitudes`, { withCredentials : true } )
    }

   obtenerDetalleSolicitud(id: number) : Observable<ApiResponse<FichaCompletaSolicitud>>{
  return this.http.get<ApiResponse<FichaCompletaSolicitud>>(`${this.apiURL}/ficha/${id}`, { withCredentials : true } )
}

    eliminarSolicitud(id : number): Observable<ApiResponse<string>> {
      return this.http.delete<ApiResponse<string>>(`${this.apiURL}/eliminar/${id}`, { withCredentials : true })

    }
}
