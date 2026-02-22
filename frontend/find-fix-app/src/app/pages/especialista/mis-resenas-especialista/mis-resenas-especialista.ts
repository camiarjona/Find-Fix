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

  public todasLasResenas: MostrarResenaEspecialistaDTO[] = [];
  public resenasVisibles = signal<MostrarResenaEspecialistaDTO[]>([]);
  public isLoading = signal(true);

  public currentPage = signal(0);
  public pageSize = 4;
  public totalPages = signal(0);

  public promedio = computed(() => {
    const lista = this.todasLasResenas;
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
        this.todasLasResenas = response.data || [];
        this.calcularPaginacion();
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error cargando reseñas', err);
        this.isLoading.set(false);
      }
    });
  }

  calcularPaginacion() {
    this.totalPages.set(Math.ceil(this.todasLasResenas.length / this.pageSize));
    this.currentPage.set(0);
    this.actualizarVistaPaginada();
  }

  actualizarVistaPaginada() {
    const inicio = this.currentPage() * this.pageSize;
    const fin = inicio + this.pageSize;
    this.resenasVisibles.set(this.todasLasResenas.slice(inicio, fin));
  }

  cambiarPagina(nuevaPagina: number) {
    this.currentPage.set(nuevaPagina);
    this.actualizarVistaPaginada();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  getEstrellas(puntuacion: number): number[] {
    const estrellas = [];
    for (let i = 1; i <= 5; i++) {
      estrellas.push(i <= puntuacion ? 1 : 0);
    }
    return estrellas;
  }
}
