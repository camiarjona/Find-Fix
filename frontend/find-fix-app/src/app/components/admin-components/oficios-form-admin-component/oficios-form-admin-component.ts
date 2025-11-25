import { Component, inject, signal } from '@angular/core';
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

  submitOficio(): void {
    if (!this.oficioName.trim()) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.oficiosService.addOficio(this.oficioName).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.oficioName = '';
        // Opcional: Cerrar el formulario automáticamente tras guardar
        // this.oficiosService.formStatus.set('hidden');
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'No se pudo agregar el oficio.');
      }
    });
  }

  cancel(): void {
    this.oficioName = '';
    this.errorMessage.set(null);
    // Esto activa la animación de cierre en el componente padre
    this.oficiosService.formStatus.set('hidden');
  }
}
