import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';

// Componentes
import { AdminDialogConfirm } from '../admin-dialog-confirm/admin-dialog-confirm';
import { OficiosFormAdminComponent } from "../oficios-form-admin-component/oficios-form-admin-component";

// Servicios y Modelos
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

  // Signals del servicio
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
      next: () => this.isLoading.set(false),
      error: (err) => {
        this.isLoading.set(false);
        this.displayPageMessage('Error al conectar con el servidor', 'error');
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
      const { id } = this.currentOffcioToDelete;

      this.oficiosService.deleteOficio(id).subscribe({
        next: (response) => {
          // El mensaje del backend o uno por defecto
          this.displayPageMessage(response.mensaje || 'Oficio eliminado correctamente', 'success');
        },
        error: (err: HttpErrorResponse) => {
          const msg = err.error?.mensaje || 'No se pudo eliminar el oficio';
          this.displayPageMessage(msg, 'error');
        }
      });
    }
    // Reset
    this.currentOffcioToDelete = { id: null, nombre: null };
  }

  mostrarAvisoEdicion(): void {
    this.displayPageMessage('La edición estará disponible próximamente', 'error');
  }

  private displayPageMessage(message: string, type: 'success' | 'error'): void {
    this.pageMessage.set({ visible: true, message, type });
    setTimeout(() => {
      this.pageMessage.set({ visible: false, message: '', type: 'success' });
    }, 3500);
  }
}
