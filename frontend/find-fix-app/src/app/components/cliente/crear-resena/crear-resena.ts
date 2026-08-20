import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { CrearResenaDTO } from '../../../models/reseña/reseña.model';
import { ResenaService } from '../../../services/reseña/reseñas.service';
import { ModalFeedbackComponent } from '../../general/modal-feedback.component/modal-feedback.component';

@Component({
  selector: 'app-crear-resena',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, ModalFeedbackComponent],
  templateUrl: './crear-resena.html',
  styleUrls: ['./crear-resena.css'],
})
export class CrearResenaComponent implements OnInit {

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private resenaService = inject(ResenaService);

  public feedbackData = { visible: false, tipo: 'success' as 'success' | 'error', titulo: '', mensaje: '' };

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

cerrarFeedback() {
    this.feedbackData.visible = false;
    if (this.feedbackData.tipo === 'success') {
      this.router.navigate(['/cliente/mis-resenas']);
    }
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
        this.feedbackData = { 
          visible: true, 
          tipo: 'success', 
          titulo: '¡Éxito!', 
          mensaje: '¡Gracias por tu opinión!' 
        };
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Error al crear reseña:', err);
        
        // Extraemos el mensaje exacto que manda tu backend de Java (ej: "Ya existe una reseña para este trabajo")
        const mensajeError = err.error?.mensaje || err.error?.message || 'Ocurrió un error al enviar tu reseña.';
        
        this.feedbackData = { 
          visible: true, 
          tipo: 'error', 
          titulo: 'Atención', 
          mensaje: mensajeError 
        };
      }
    });
  }

  cancelar() {
    this.router.navigate(['/cliente/mis-trabajos']);
  }

  
}
