import { Component, inject, signal } from '@angular/core';
import { OficiosService } from '../../../services/admin-services/oficios-service';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-oficios-form-admin-component',
  imports: [CommonModule,FormsModule],
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
      this.oficiosService.formStatus.set('hidden');
  }
}

