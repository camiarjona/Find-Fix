import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MostrarResenaEspecialistaDTO } from '../../../models/reseña/reseña.model';
import { ResenaService } from '../../../services/reseña/reseñas.service';

@Component({
  selector: 'app-mis-resenas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-resenas-especialista.html',
  styleUrl: './mis-resenas-especialista.css',
})
export class MisResenasPage implements OnInit {

  private resenaService = inject(ResenaService);

  // Estado
  public resenas = signal<MostrarResenaEspecialistaDTO[]>([]);
  public isLoading = signal(true);

  public promedio = computed(() => {
    const lista = this.resenas();
    if (lista.length === 0) return 0;

    const suma = lista.reduce((acc, curr) => acc + curr.puntuacion, 0);
    return (suma / lista.length).toFixed(1);
  });

  ngOnInit() {
    this.cargarResenas();
  }

  cargarResenas() {
    this.isLoading.set(true);
    this.resenaService.obtenerResenasRecibidas().subscribe({
      next: (response) => {
        this.resenas.set(response.data || []);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error cargando reseñas', err);
        this.isLoading.set(false);
      }
    });
  }

  getEstrellas(puntuacion: number): number[] {
    const estrellas = [];
    for (let i = 1; i <= 5; i++) {
      estrellas.push(i <= puntuacion ? 1 : 0);
    }
    return estrellas;
  }
}
