import { Component, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { OficiosService } from '../../../services/admin-services/oficios-service';

@Component({
  selector: 'app-oficios-form-admin-component',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './oficios-form-admin-component.html',
  styleUrl: './oficios-form-admin-component.css',
})
export class OficiosFormAdminComponent {
  private oficiosService = inject(OficiosService);

  public oficioName: string = '';
  public isLoading = signal(false);
  public errorMessage = signal<string | null>(null);

  public formStatus = this.oficiosService.formStatus;

  constructor() {
    effect(() => {
      const selected = this.oficiosService.selectedOficio();
      if (selected) {
        // MODO EDICIÓN
        this.oficioName = selected.nombre;
      } else {
        // MODO CREACIÓN
        this.oficioName = '';
      }
    });
  }

  submitOficio(): void {
    if (!this.oficioName.trim()) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const currentMode = this.oficiosService.formStatus();

    if (currentMode === 'editing') {
      this.handleUpdate();
    } else {
      this.handleCreate();
    }
  }

    private finalizeAction() {
    this.isLoading.set(false);
    this.oficioName = '';
    // this.oficiosService.formStatus.set('hidden');
  }
  
  // create
  private handleCreate() {
    this.oficiosService.addOficio(this.oficioName).subscribe({
      next: () => {
        this.finalizeAction();
      },
      error: (err: HttpErrorResponse) => {
        this.handleError(err);
      }
    });
  }

  // update
  private handleUpdate() {
    const selected = this.oficiosService.selectedOficio();
    if (!selected) return;

    this.oficiosService.updateOficio(selected.id, this.oficioName).subscribe({
      next: () => {
        this.finalizeAction();
        this.oficiosService.selectedOficio.set(null);
      },
      error: (err: HttpErrorResponse) => {
        this.handleError(err);
      }
    });
  }

  cancel(): void {
    this.oficioName = '';
    this.errorMessage.set(null);
    // Esto activa la animación de cierre en el componente padre
    this.oficiosService.formStatus.set('hidden');
  }

  private handleError(err: HttpErrorResponse) {
    this.isLoading.set(false);
    this.errorMessage.set(err.error?.mensaje || 'Ocurrió un error al procesar la solicitud.');
  }

}
