import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResenaService } from '../../../services/reseña/reseñas.service';
import { MostrarResenaClienteDTO } from '../../../models/reseña/reseña.model';

@Component({
  selector: 'app-mis-resenas-enviadas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-resenas-enviadas-cliente.html',
  styleUrls: ['./mis-resenas-enviadas-cliente.css'],
})
export class MisResenasEnviadasCliente implements OnInit {

  private resenaService = inject(ResenaService);

  // Estado
  public resenas = signal<MostrarResenaClienteDTO[]>([]);
  public isLoading = signal(true);

  // Promedio de las calificaciones que has dado (opcional, pero queda bien)
  public promedio = computed(() => {
    const lista = this.resenas();
    if (lista.length === 0) return 0;
    const suma = lista.reduce((acc, curr) => acc + curr.puntuacion, 0);
    return (suma / lista.length).toFixed(1);
  });

  ngOnInit() {
    this.cargarResenasEnviadas();
  }

  cargarResenasEnviadas(): void {
    this.isLoading.set(true);

    this.resenaService.obtenerResenasEnviadas().subscribe({
      next: (response) => {
        this.resenas.set(response.data || []);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar las reseñas enviadas:', err);
        this.isLoading.set(false);
      }
    });
  }

  // Helper para estrellas
  getEstrellas(puntuacion: number): number[] {
    const estrellas = [];
    for (let i = 1; i <= 5; i++) {
      estrellas.push(i <= puntuacion ? 1 : 0);
    }
    return estrellas;
  }
}
