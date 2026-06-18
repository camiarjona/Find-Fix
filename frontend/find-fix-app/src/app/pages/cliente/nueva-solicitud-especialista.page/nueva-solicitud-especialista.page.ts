import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SolicitudEspecialistaService } from '../../../services/cliente/solicitud-especialista.service';
import { CommonModule } from '@angular/common';
import { ModalConfirmacionComponent } from "../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component";
import { NgxDropzoneModule } from 'ngx-dropzone';

@Component({
  selector: 'app-nueva-solicitud-especialista.page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalConfirmacionComponent, NgxDropzoneModule],
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

  fotosRequisitos = signal<{ file: File, url: string }[]>([]);

  onSelectFotosRequisitos(event: any): void {
    const totalActual = this.fotosRequisitos().length;
    const nuevosArchivos = event.addedFiles;

    if (totalActual + nuevosArchivos.length > 5) {
      this.mensajeError.set('⚠️ Solo podés subir un máximo de 5 fotos (DNI + 3 trabajos).');
      return;
    }

    this.mensajeError.set(null);

    // Mapeamos cada archivo generando su URL local instantánea con URL.createObjectURL
    const nuevosConPreview = nuevosArchivos.map((file: File) => ({
      file: file,
      url: URL.createObjectURL(file) // 👈 Esto salta el lag de carga al toque
    }));

    this.fotosRequisitos.update(current => [...current, ...nuevosConPreview]);
  }

  onRemoveFotoRequisito(item: { file: File, url: string }): void {
    // Liberamos memoria del navegador de la URL creada
    URL.revokeObjectURL(item.url);
    this.fotosRequisitos.update(current => current.filter(f => f !== item));
  }

  enviarSolicitud(): void {
    if (this.solicitudForm.invalid || this.fotosRequisitos().length === 0) {
      this.solicitudForm.markAllAsTouched();
      return;
    }

    this.mensajeError.set(null);
    const formData = new FormData();

    const datosDTO = { motivo: this.solicitudForm.value.motivo };
    formData.append('datos', new Blob([JSON.stringify(datosDTO)], { type: 'application/json' }));

    // 👈 Enviamos la propiedad .file al backend
    this.fotosRequisitos().forEach(item => {
      formData.append('fotos', item.file);
    });

    this.solicitudService.enviarSolicitud(formData).subscribe({
      next: () => {
        this.solicitudForm.reset();
        this.fotosRequisitos().forEach(item => URL.revokeObjectURL(item.url)); // Limpieza
        this.fotosRequisitos.set([]);
        this.isModalOpen.set(true);
      },
      error: (err) => this.mensajeError.set(err.error.message || 'Error al enviar la solicitud')
    });
  }

  onModalIrHistorial() {
    this.isModalOpen.set(false);
    this.router.navigate(['/cliente/solicitar-especialista/historial']);
  }

  onModalIrInicio() {
    this.isModalOpen.set(false);
    this.router.navigate(['/cliente/dashboard']);
  }

  onModalCerrar() {
    this.isModalOpen.set(false);
  }
}
