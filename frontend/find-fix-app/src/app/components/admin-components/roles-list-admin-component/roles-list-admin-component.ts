import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RolesService } from '../../../services/admin-services/roles-service';
import { RolToDelete } from '../../../models/admin-models/rol-model';
import { AdminDialogConfirm } from "../admin-dialog-confirm/admin-dialog-confirm";
import { RolFormAdminComponent } from "../rol-form-admin-component/rol-form-admin-component"; // IMPORTAR

@Component({
  selector: 'app-roles-list-admin-component',
  standalone: true,
  // Agregamos RolesFormAdminComponent a los imports
  imports: [CommonModule, AdminDialogConfirm, RolFormAdminComponent],
  templateUrl: './roles-list-admin-component.html',
  styleUrl: './roles-list-admin-component.css',
})
export class RolesListAdminComponent implements OnInit {

  private rolesService = inject(RolesService);

  public roles = this.rolesService.roles;
  public formStatus = this.rolesService.formStatus;

  public showConfirmDialog = signal(false);
  public currentRolToDelete: RolToDelete = { nombre: null };

  public pageMessage = signal({ visible: false, message: '', type: 'success' });
  public isLoading = signal(true);

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles() {
    this.isLoading.set(true);
    this.rolesService.getRoles().subscribe({
      next: () => this.isLoading.set(false),
      error: (err) => {
        this.isLoading.set(false);
        this.showMessage('Error al cargar los roles', 'error');
      }
    });
  }

  toggleForm(): void {
    const current = this.formStatus();
    this.rolesService.formStatus.set(current === 'hidden' ? 'creating' : 'hidden');
  }

  confirmarEliminacion(nombre: string): void {
    this.currentRolToDelete = { nombre };
    this.showConfirmDialog.set(true);
  }

  handleDelete(confirmed: boolean): void {
    this.showConfirmDialog.set(false);

    if (confirmed && this.currentRolToDelete.nombre) {
      this.rolesService.deleteRol(this.currentRolToDelete.nombre).subscribe({
        next: () => this.showMessage('Rol eliminado con éxito ✔️', 'success'),
        error: (err) => this.showMessage(err.error?.mensaje || 'Error al eliminar', 'error')
      });
    }
  }

  showMessage(msg: string, type: string) {
    this.pageMessage.set({ visible: true, message: msg, type });
    setTimeout(() => this.pageMessage.set({ ...this.pageMessage(), visible: false }), 3500);
  }

  mostrarAvisoEdicion(): void {
    this.showMessage('⚠️ Esta opción se encuentra deshabilitada momentáneamente.', 'error');
  }
}
