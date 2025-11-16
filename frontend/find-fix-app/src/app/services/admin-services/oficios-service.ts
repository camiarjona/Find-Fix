import { Injectable, signal } from '@angular/core';
import { OficioModel } from '../../models/admin-models/oficio-model';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../../models/api-response/apiResponse.model';

@Injectable({
  providedIn: 'root',
})
export class OficiosService {
  private apiUrl = '/oficios';
  private oficiosState = signal<OficioModel[]>([]);
  oficios = this.oficiosState.asReadonly();
  constructor(private http: HttpClient)
  {
    /// aca metemos el metodo get de oficios
    this.getOficios();
  }


  getOficios()
  {
    this.http.get<ApiResponse<OficioModel[]>>(this.apiUrl).subscribe({
      next: (response) => {
        // 2. Accede a la propiedad 'data' para obtener el array de oficios
        this.oficiosState.set(response.data);
        console.log('Oficios cargados con éxito:', response.data);
      },
      error: (err) => {
        // 3. Implementa manejo de errores para verificar 403 Forbidden o CORS
        console.error('Error al cargar oficios (Código HTTP:', err.status, ')', err);
      }
    });
  }
}
