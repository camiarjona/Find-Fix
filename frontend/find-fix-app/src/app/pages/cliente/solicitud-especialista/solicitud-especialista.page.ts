import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MostrarSolicitud } from '../../../models/cliente/solicitud-especialista.model';
import { SolicitudEspecialistaService } from '../../../services/cliente/solicitud-especialista.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-solicitud-especialista',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './solicitud-especialista.page.html',
  styleUrl: './solicitud-especialista.page.css',
})
export class SolicitudEspecialista {

  private fb = inject(FormBuilder);
  private solicitudService = inject(SolicitudEspecialistaService);


  solicitudForm: FormGroup = this.fb.group({
    motivo: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(250)]]
  });

  misSolicitudes = signal<MostrarSolicitud[]>([]);
  mensajeError = signal<string | null>(null);
  mensajeExito = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes(): void {
    this.solicitudService.obtenerMisSolicitudes().subscribe({
      next: (response) => {
        this.misSolicitudes.set(response.data);
        this.mensajeError.set(null);
      },
      error: (err) => {
        this.mensajeError.set(err.error.message || 'Error al cargar solicitudes');
        console.error(err);
      }
    });
  }
  enviarSolicitud(): void {
      if (this.solicitudForm.invalid) return;

      this.solicitudService.enviarSolicitud(this.solicitudForm.value).subscribe({
        next: () => {
          this.mensajeError.set(null);
          this.cargarSolicitudes();
          this.solicitudForm.reset();
        },
        error: (err) => this.mensajeError.set(err.error.message || 'Error al enviar la solicitud')
      });
    }

    eliminarSolicitud(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta solicitud?')) {
      this.solicitudService.eliminarSolicitud(id).subscribe({
        next: () => {
          this.mensajeExito.set('Solicitud eliminada con éxito.');
          this.mensajeError.set(null);
          this.cargarSolicitudes();
        },
        error: (err) => this.mensajeError.set(err.error.message || 'Error al eliminar la solicitud')
      });
    }
  }

}

