import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { UserService } from '../../../services/user/user.service';
import { UpdateUserRequest, UserProfile } from '../../../models/user/user.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-modal-detalle-usuario',
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-detalle-usuario.html',
  styleUrl: './modal-detalle-usuario.css',
})
export class ModalDetalleUsuario {

  private userService = inject(UserService);

  @Input({ required: true }) usuario!: UserProfile;
  @Output() cerrar = new EventEmitter<void>();
  @Output() usuarioActualizado = new EventEmitter<void>(); // Para avisar a la tabla

  // --- LÓGICA DE EDICIÓN ---
  public editingField = signal<string | null>(null);
  public tempValue = '';
  public isLoading = signal(false);

  startEdit(field: string, currentValue: string | undefined) {
    this.editingField.set(field);
    this.tempValue = currentValue || '';
  }

  cancelEdit() {
    this.editingField.set(null);
    this.tempValue = '';
  }

  saveEdit(field: string) {
    if (!this.tempValue.trim()) return;

    this.isLoading.set(true);

    const updateData: UpdateUserRequest = { [field]: this.tempValue };

    // LLAMAMOS AL MÉTODO DE ADMIN, USANDO EL EMAIL DEL USUARIO PARA IDENTIFICARLO
    this.userService.updateUserByAdmin(this.usuario.email, updateData).subscribe({
      next: (res) => {
        // Actualizamos el dato localmente
        (this.usuario as any)[field] = this.tempValue;

        this.isLoading.set(false);
        this.editingField.set(null);
        this.usuarioActualizado.emit(); // Avisamos al padre por si quiere refrescar
        alert('Usuario actualizado correctamente ✅');
      },
      error: (err) => {
        console.error(err);
        alert(err.error?.mensaje || 'Error al actualizar');
        this.isLoading.set(false);
      }
    });
  }
}
