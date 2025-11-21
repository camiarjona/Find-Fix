import { Component, inject, signal } from '@angular/core';
import { RolesService } from '../../../services/admin-services/roles-service';
import { RolToDelete } from '../../../models/admin-models/rol-model';
import { AdminDialogConfirm } from "../admin-dialog-confirm/admin-dialog-confirm";
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-roles-list-admin-component',
  imports: [AdminDialogConfirm, CommonModule, FormsModule],
  templateUrl: './roles-list-admin-component.html',
  styleUrl: './roles-list-admin-component.css',
})
export class RolesListAdminComponent {
  private rolesService = inject(RolesService)
  public roles = this.rolesService.roles;
  public formStatus = this.rolesService.formStatus;
  public showConfirmDialog = signal(false);
  public currentRolToDelete: RolToDelete = { nombre: null };
  public pageMessage = signal({ visible: false, message: '', type: 'success' });
  public nuevoRolNombre = '';

  public isLoading = signal(true);

  ngOnInit(): void {
    console.log("Iniciando carga de roles...");

    this.isLoading.set(true);
    this.rolesService.getRoles().subscribe({
      next: () => {
        this.isLoading.set(false);
        console.log("Carga completa, loader apagado.");
      },
      error: (err) => {
        this.isLoading.set(false);
        this.showMessage('Error al cargar los roles', err);
      }
    });
  }

  toggleForm(): void {
    if (this.formStatus() === 'hidden') {
      this.rolesService.formStatus.set('creating');
    } else {
      this.rolesService.formStatus.set('hidden');
      this.nuevoRolNombre = '';
    }
  }
  submitRol(): void {
    if (!this.nuevoRolNombre.trim()) return;

    this.rolesService.addRol(this.nuevoRolNombre).subscribe({
      next: () => {
        this.showMessage('Rol creado con éxito', 'success');
        this.nuevoRolNombre = '';
      },
      error: (err) => this.showMessage(err.error?.mensaje || 'Error al crear', 'error')
    });
  }

  confirmarEliminacion(nombre: string): void {
    this.currentRolToDelete = { nombre };
    this.showConfirmDialog.set(true);
  }
  handleDelete(confirmed: boolean): void {
    this.showConfirmDialog.set(false);

    if (confirmed && this.currentRolToDelete.nombre) {
      this.rolesService.deleteRol(this.currentRolToDelete.nombre).subscribe({
        next: (res) => this.showMessage(res.mensaje, 'success'),
        error: (err) => this.showMessage(err.error?.mensaje || 'Error al eliminar', 'error')
      });
    }
  }
  showMessage(msg: string, type: string) {
    this.pageMessage.set({ visible: true, message: msg, type });
    setTimeout(() => this.pageMessage.set({ ...this.pageMessage(), visible: false }), 3000);
  }

  mostrarAvisoEdicion(): void {
    this.showMessage('⚠️ Esta opción se encuentra deshabilitada momentáneamente.', 'error');
  }
}


