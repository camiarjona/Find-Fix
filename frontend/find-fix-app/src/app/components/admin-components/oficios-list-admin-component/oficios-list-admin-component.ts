import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';

import { AdminDialogConfirm } from '../admin-dialog-confirm/admin-dialog-confirm';
import { OficiosFormAdminComponent } from "../oficios-form-admin-component/oficios-form-admin-component";
import { OficiosService } from '../../../services/admin-services/oficios-service';
import { messageConfirm, OficioToDelete } from '../../../models/admin-models/oficio-model';

@Component({
  selector: 'app-oficios-list-admin-component',
  imports: [CommonModule, AdminDialogConfirm, OficiosFormAdminComponent],
  templateUrl: './oficios-list-admin-component.html',
  styleUrl: './oficios-list-admin-component.css',
})
export class OficiosListAdminComponent implements OnInit {

  private oficiosService = inject(OficiosService);

  public oficios = this.oficiosService.oficios;
  public formStatus = this.oficiosService.formStatus;

  public isLoading = signal(true);
  public showConfirmDialog = signal(false);
  public currentOffcioToDelete: OficioToDelete = { id: null, nombre: null };

  public pageMessage = signal<messageConfirm>({
    visible: false,
    message: '',
    type: 'success',
  });

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    this.oficiosService.getOficios().subscribe({
      next: () => {
        this.isLoading.set(false);
        console.log("Carga de oficios completa.");
      },
      error: (err) => {
        this.isLoading.set(false);
        this.displayPageMessage('Error al cargar los oficios', 'error');
      }
    });
  }

  toggleForm(): void {
    const current = this.formStatus();
    this.oficiosService.formStatus.set(current === 'hidden' ? 'creating' : 'hidden');
  }

  eliminarOficio(id: number, nombre: string): void {
    this.currentOffcioToDelete = { id, nombre };
    this.showConfirmDialog.set(true);
  }

  handleDeleteConfirmation(confirmed: boolean): void {
    this.showConfirmDialog.set(false);

    if (confirmed && this.currentOffcioToDelete.id !== null) {
      const { id, nombre } = this.currentOffcioToDelete;

      this.oficiosService.deleteOficio(id).subscribe({
        next: (response) => {
          const successMessage = response.mensaje || `Oficio "${nombre}" eliminado correctamente.`;
          this.displayPageMessage(successMessage, 'success');
        },
        error: (err: HttpErrorResponse) => {
          const errorMessage = err.error?.mensaje || 'Error desconocido al eliminar el oficio.';
          this.displayPageMessage(errorMessage, 'error');
        }
      });
    }
    this.currentOffcioToDelete = { id: null, nombre: null };
  }

  mostrarAvisoEdicion(): void {
    this.displayPageMessage('⚠️ Esta opción se encuentra deshabilitada momentáneamente.', 'error');
  }

  displayPageMessage(message: string, type: 'success' | 'error'): void {
    this.pageMessage.set({ visible: true, message, type });
    setTimeout(() => {
      this.pageMessage.set({ visible: false, message: '', type: 'success' });
    }, 3000);
  }
}
