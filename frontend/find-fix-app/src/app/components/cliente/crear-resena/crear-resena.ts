import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { CrearResenaDTO } from '../../../models/reseña/reseña.model';
import { ResenaService } from '../../../services/reseña/reseñas.service';

@Component({
  selector: 'app-crear-resena',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    RouterLink
],
  templateUrl: './crear-resena.html',
  styleUrls: ['./crear-resena.css'],
})
export class CrearResenaComponent implements OnInit {

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private resenaService = inject(ResenaService);
  private snackBar = inject(MatSnackBar);

  resenaForm!: FormGroup;
  trabajoId!: number;
  isLoading: boolean = false;

  ngOnInit(): void {
    // 1. Obtener el trabajoId de la URL
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('trabajoId');
      if (idParam) {
        this.trabajoId = +idParam; // Convertir a número
      } else {
        // Manejar el caso de ID faltante: redirigir o mostrar error
        this.snackBar.open('Error: ID de trabajo no encontrado.', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/cliente/mis-trabajos']); // Redirigir a los trabajos del cliente
      }
    });

    // 2. Inicializar el formulario reactivo
    this.resenaForm = this.fb.group({
      puntuacion: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comentario: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  onSubmit(): void {
    if (this.resenaForm.invalid || this.isLoading) {
      this.resenaForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    // Crear el DTO a enviar
    const formValue = this.resenaForm.value;
    const resenaDTO: CrearResenaDTO = {
      puntuacion: formValue.puntuacion,
      comentario: formValue.comentario,
      trabajoId: this.trabajoId // El ID que obtuvimos de la ruta
    };

    this.resenaService.crearResena(resenaDTO).subscribe({
      next: (response) => {
        this.isLoading = false;
        // Mostrar mensaje de éxito
        this.snackBar.open('¡Reseña enviada con éxito!', 'OK', { duration: 3000 });
        // Redirigir al listado de reseñas enviadas o al historial de trabajos
        this.router.navigate(['/cliente/mis-resenas-enviadas']);
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error al crear la reseña:', err);
        const mensajeError = err.error?.mensaje || 'Error al enviar la reseña. Intenta de nuevo.';
        this.snackBar.open(mensajeError, 'Cerrar', { duration: 5000 });
      }
    });
  }

  // Helper para control de errores en el template
  get f() {
    return this.resenaForm.controls;
  }
}
