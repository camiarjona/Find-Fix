import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model';
import { AgregarFavoritoDTO, FavoritoModel } from '../../models/favoritos/lista-favs.model';

@Injectable({
  providedIn: 'root',
})
export class FavoritoService {

  private apiUrl = 'http://localhost:8080';
  private http = inject(HttpClient);
  private favoritosUrl = `${this.apiUrl}/favoritos`;

  agregarFavorito(data: AgregarFavoritoDTO): Observable<ApiResponse<FavoritoModel>> {
    return this.http.post<ApiResponse<FavoritoModel>>(`${this.favoritosUrl}/${data.especialistaEmail}`, data);
  }

  eliminarFavorito(especialistaEmail: string): Observable<ApiResponse<void>> {
    const url = `${this.favoritosUrl}/eliminar/${especialistaEmail}`;
    return this.http.delete<ApiResponse<void>>(url);
  }

  obtenerFavoritosPorCliente(): Observable<ApiResponse<FavoritoModel[]>> {
    const url = this.favoritosUrl;
    return this.http.get<ApiResponse<FavoritoModel[]>>(url);
  }
}
