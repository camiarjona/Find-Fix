import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FotoPerfilService {
  // Esta URL debe coincidir con el @RequestMapping del FotoPerfilController
  private apiUrl = 'http://localhost:8080/api/usuarios/foto';

  constructor(private http: HttpClient) { }

  subirFoto(file: File, usuarioId: number): Observable<any> {
    const formData = new FormData();
    // Importante: 'file' debe ser el mismo nombre que pusiste en @RequestParam("file")
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/subir/${usuarioId}`, formData);
  }
}
