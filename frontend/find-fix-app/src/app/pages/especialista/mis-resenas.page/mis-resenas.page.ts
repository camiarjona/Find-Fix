import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { EspecialistaService } from '../../../services/especialista/especialista.service';
import { ResenaEspecialista } from '../../../models/especialista/especialista.model';

@Component({
  selector: 'app-mis-resenas.page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-resenas.page.html',
  styleUrl: './mis-resenas.page.css',
})
export class MisResenasPage {

   private especialistaService = inject(EspecialistaService);
  resenas = signal<ResenaEspecialista[]>([]);
  promedio = signal(0);

  // Importante: Exponemos Math al HTML para poder usar Math.round() en las estrellas
  protected readonly Math = Math;

  ngOnInit() {
    // this.cargarResenasReales(); // <--- MANTENER COMENTADO HASTA TENER BACKEND
    this.cargarDatosFalsos();      // <--- USAMOS ESTO AHORA
  }

  cargarDatosFalsos() {
    const dataMock: ResenaEspecialista[] = [
      {
        resenaId: 1,
        puntuacion: 5,
        nombreCliente: 'Julián Álvarez',
        comentario: '¡Excelente servicio! Llegó súper puntual, fue muy amable y solucionó el problema del aire acondicionado en 20 minutos. Muy recomendable.'
      },
      {
        resenaId: 2,
        puntuacion: 4,
        nombreCliente: 'Enzo Fernández',
        comentario: 'Buen trabajo en general. El precio me pareció un poco elevado para lo que era, pero la calidad es indiscutible y quedó todo funcionando perfecto.'
      },
      {
        resenaId: 3,
        puntuacion: 5,
        nombreCliente: 'Lionel Messi',
        comentario: 'Un genio total. Me salvó un domingo a la tarde con una urgencia eléctrica. Gracias por la buena onda.'
      },
      {
        resenaId: 4,
        puntuacion: 3,
        nombreCliente: 'Lautaro Martínez',
        comentario: 'El trabajo quedó bien, pero demoró más de lo pactado en llegar y no avisó.'
      },
      {
        resenaId: 5,
        puntuacion: 5,
        nombreCliente: 'Paulo Dybala',
        comentario: 'Impecable. Dejó todo limpio después de trabajar. Volveré a contratar sin dudarlo.'
      }
    ];

    // Asignamos los datos falsos a la señal
    this.resenas.set(dataMock);

    // Calculamos el promedio automáticamente basado en los datos falsos
    if(dataMock.length > 0) {
      const suma = dataMock.reduce((acc, r) => acc + r.puntuacion, 0);
      this.promedio.set(parseFloat((suma / dataMock.length).toFixed(1)));
    }
  }

  getEstrellas(puntuacion: number): number[] {
    return Array(5).fill(0).map((_, i) => i < puntuacion ? 1 : 0);
  }
}
