import { Component, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms'; // Módulos de Formulario
import { SolicitudEspecialistaAdminService } from '../../../services/admin-services/solicitud-especialista-admin';
import { FichaCompletaSolicitud, ActualizarSolicitudDTO } from '../../../models/cliente/solicitud-especialista.model';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-solicitud-detalle-admin',
  standalone: true,
  imports: [CommonModule, DatePipe, ReactiveFormsModule],
  templateUrl: './solicitud-detalle-admin.html',
  styleUrl: './solicitud-detalle-admin.css',
})
export class SolicitudDetalleAdminComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private solicitudService = inject(SolicitudEspecialistaAdminService);

  // Estado para la solicitud completa
  public solicitud: WritableSignal<FichaCompletaSolicitud | null> = signal(null);
  public loading = signal(false);
  public isResolving = signal(false);
  public submissionError = signal<string | null>(null);

  // Formulario de Resolución
  public resolutionForm: FormGroup = this.fb.group({
    respuesta: ['', [Validators.required, Validators.maxLength(500)]],
    estado: ['', [Validators.required]], // 'APROBADO' o 'RECHAZADO'
  });

  // Determina si se puede mostrar el formulario de resolución
  public get isPending() {

  const solicitudActual = this.solicitud();

  if (!solicitudActual) {
    return false;
  }
  return solicitudActual.estado.toString() === 'PENDIENTE';

}

  ngOnInit(): void {
    this.loadSolicitud();
  }

  loadSolicitud(): void {
    this.loading.set(true);
    this.route.paramMap.pipe(
      switchMap(params => {
        const idString = params.get('id');
        const id = idString ? +idString : null;

        if (!id) {
          //this.router.navigate(['/admin/solicitudes']); // Redirigir si no hay ID
          return [];
        }

        return this.solicitudService.getFichaCompleta(id);
      })
    ).subscribe({
      next: (response) => {
        if (response.data) {
          this.solicitud.set(response.data as FichaCompletaSolicitud);
        } else {
          // this.router.navigate(['/admin/solicitudes']);
          console.warn("Backend devolvió data: null para este ID.");
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar la ficha:', err);
        this.loading.set(false);
        // this.router.navigate(['/admin/solicitudes']);
      }
    });
  }

  // Método para manejar la resolución (Aprobar o Rechazar)
  handleResolution(estado: 'APROBADO' | 'RECHAZADO'): void {
    this.resolutionForm.get('estado')?.setValue(estado);

    if (this.resolutionForm.invalid) {
      this.submissionError.set("La respuesta es obligatoria y el estado debe estar seleccionado.");
      this.resolutionForm.markAllAsTouched();
      return;
    }

    const id = this.solicitud()!.seId;
    this.isResolving.set(true);
    this.submissionError.set(null);

    const dto: ActualizarSolicitudDTO = {
        estado: estado,
        respuesta: this.resolutionForm.value.respuesta,
    };

    this.solicitudService.actualizarEstado(id, dto).subscribe({
      next: (response) => {
        alert(`Solicitud ${estado} con éxito.`);
        this.isResolving.set(false);
        // Recargar la ficha con la nueva respuesta del backend
        this.loadSolicitud();
      },
      error: (err) => {
        console.error('Error al resolver la solicitud:', err);
        this.submissionError.set('Fallo al contactar el servidor para la resolución.');
        this.isResolving.set(false);
      }
    });
  }

  // Función para redirigir (necesaria para el botón 'Volver')
  goBack(): void {
    this.router.navigate(['/admin/solicitudes']);
  }
}
