import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../../models/api-response/apiResponse.model';

@Injectable({
  providedIn: 'root'
})
export class FotoTrabajoService {
  private apiUrl = `http://localhost:8080/api/fotos-trabajo`;

  constructor(private http: HttpClient) { }

  /**
  @param fotos
   */
  subirFotos(fotos: File[]): Observable<ApiResponse<any>> {
    const formData = new FormData();

    fotos.forEach(foto => {
      formData.append('file', foto);
    });

    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/subir-galeria`, formData);
  }

  /**
  @param id
   */
  eliminarFoto(id: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/eliminar/${id}`, { withCredentials: true });
  }

}
