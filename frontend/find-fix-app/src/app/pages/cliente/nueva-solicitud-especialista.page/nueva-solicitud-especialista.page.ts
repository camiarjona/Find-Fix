import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SolicitudEspecialistaService } from '../../../services/cliente/solicitud-especialista.service';
import { CommonModule } from '@angular/common';
import { ModalConfirmacionComponent } from "../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component";

@Component({
  selector: 'app-nueva-solicitud-especialista.page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalConfirmacionComponent],
  templateUrl: './nueva-solicitud-especialista.page.html',
  styleUrl: './nueva-solicitud-especialista.page.css',
})
export class NuevaSolicitudEspecialistaPage {

  private fb = inject(FormBuilder);
  private solicitudService = inject(SolicitudEspecialistaService);
  private router = inject(Router);

  solicitudForm: FormGroup = this.fb.group({
    motivo: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(250)]]
  });

  mensajeError = signal<string | null>(null);
  isModalOpen = signal(false);

  enviarSolicitud(): void {
    if (this.solicitudForm.invalid) {
      this.solicitudForm.markAllAsTouched();
      return;
    }

    this.mensajeError.set(null);

    this.solicitudService.enviarSolicitud(this.solicitudForm.value).subscribe({
      next: () => {
        this.solicitudForm.reset();
        this.isModalOpen.set(true);
      },
      error: (err) => this.mensajeError.set(err.error.message || 'Error al enviar la solicitud')
    });
  }

    //Funciones para manejar los eventos del modal

  onModalIrInicio() {
    this.isModalOpen.set(false);
    this.router.navigate(['/app/dashboard']);
  }

  onModalIrHistorial() {
    this.isModalOpen.set(false);
    this.router.navigate(['/app/solicitar-especialista/historial']);
  }

  onModalCerrar() {
    this.isModalOpen.set(false);
  }

}
