import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { CrearResenaDTO } from '../../../models/reseña/reseña.model';
import { ResenaService } from '../../../services/reseña/reseñas.service';

@Component({
  selector: 'app-crear-resena',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './crear-resena.html',
  styleUrls: ['./crear-resena.css'],
})
export class CrearResenaComponent implements OnInit {

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private resenaService = inject(ResenaService);

  resenaForm!: FormGroup;
  trabajoId!: number;

  // Estado con Signals
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  puntuacionSeleccionada = signal(5); // Para control visual de estrellas

  ngOnInit(): void {
    // 1. Obtener ID del trabajo
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('trabajoId');
      if (idParam) {
        this.trabajoId = +idParam;
      } else {
        alert('Error: No se especificó el trabajo a calificar.');
        this.router.navigate(['/cliente/mis-trabajos']);
      }
    });

    // 2. Inicializar formulario
    this.resenaForm = this.fb.group({
      puntuacion: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comentario: ['', [Validators.required, Validators.maxLength(500), Validators.minLength(10)]]
    });

    // Sincronizar señal visual con el control de formulario
    this.resenaForm.get('puntuacion')?.valueChanges.subscribe(val => {
      this.puntuacionSeleccionada.set(val);
    });
  }

  // Método para seleccionar estrellas haciendo clic
  setEstrellas(valor: number) {
    this.resenaForm.get('puntuacion')?.setValue(valor);
  }

  onSubmit(): void {
    if (this.resenaForm.invalid || this.isLoading()) {
      this.resenaForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const dto: CrearResenaDTO = {
      puntuacion: this.resenaForm.value.puntuacion,
      comentario: this.resenaForm.value.comentario,
      trabajoId: this.trabajoId
    };

    this.resenaService.crearResena(dto).subscribe({
      next: () => {
        this.isLoading.set(false);
        alert('¡Gracias por tu opinión!');
        this.router.navigate(['/cliente/mis-resenas']);
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Error al crear reseña:', err);
        this.errorMessage.set(err.error?.mensaje || 'Ocurrió un error al enviar tu reseña.');
      }
    });
  }

  cancelar() {
    this.router.navigate(['/cliente/mis-trabajos']);
  }
}
