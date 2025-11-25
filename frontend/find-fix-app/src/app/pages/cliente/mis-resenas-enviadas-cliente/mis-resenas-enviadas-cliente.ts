import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResenaService } from '../../../services/reseña/reseñas.service';
import { MostrarResenaClienteDTO } from '../../../models/reseña/reseña.model';
import { catchError, of } from 'rxjs';
import { MatCardModule } from '@angular/material/card'; // Asumo que usas Angular Material
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-mis-resenas-enviadas',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatProgressSpinnerModule, RouterModule],
  templateUrl: './mis-resenas-enviadas-cliente.html',
  styleUrls: ['./mis-resenas-enviadas-cliente.css'],
})
export class MisResenasEnviadasCliente implements OnInit {

  private resenaService = inject(ResenaService);

  resenas: MostrarResenaClienteDTO[] = [];
  isLoading: boolean = true;
  error: string | null = null;

  ngOnInit() {
    this.cargarResenasEnviadas();
  }

  cargarResenasEnviadas(): void {
    this.isLoading = true;
    this.error = null;

    this.resenaService.obtenerResenasEnviadas().pipe( // Llama al método para obtener las reseñas enviadas
      catchError(err => {
        console.error('Error al cargar las reseñas enviadas:', err);
        this.error = 'No se pudieron cargar tus reseñas. Intenta de nuevo más tarde.';
        this.isLoading = false;
        return of({ data: [], mensaje: 'Error' } as any); // Devuelve un Observable con un array vacío en caso de error
      })
    )
    .subscribe(response => {
      this.isLoading = false;
      // Asumo que el API Response tiene una propiedad 'data' que contiene el array
      if (response && Array.isArray(response.data)) {
        this.resenas = response.data;
      } else {
        // Manejar el caso donde la API devuelve un status OK pero sin datos
        this.resenas = [];
      }
    });
  }
}
