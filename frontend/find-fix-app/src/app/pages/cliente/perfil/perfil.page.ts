import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user/user.service';
import { UserProfile, UpdateUserRequest, UpdatePasswordRequest } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';

@Component({
  selector: 'app-mi-perfil-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './perfil.page.html',
  styleUrls: ['./perfil.page.css']
})
export class PerfilPage implements OnInit {

  private userService = inject(UserService);

  public icons = UI_ICONS;

  // --- ESTADO DE DATOS ---
  public usuario = signal<UserProfile | null>(null);
  public availableCities = signal<string[]>([]);
  public isLoading = signal(true);

  // --- LÓGICA DE EDICIÓN (DATOS PERSONALES) ---
  public editingField = signal<string | null>(null);
  public tempValue = '';

  // --- LÓGICA DE SEGURIDAD (CONTRASEÑA) ---
  public passwordData: UpdatePasswordRequest & { confirmacion: string } = {
    passwordActual: '',
    passwordNuevo: '',
    confirmacion: ''
  };
  public isPasswordLoading = signal(false);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    // 1. Cargar Perfil
    this.userService.getProfile().subscribe({
      next: (res) => {
        this.usuario.set(res.data);
        this.isLoading.set(false);
      },
      error: (err) => { console.error(err); this.isLoading.set(false); }
    });

    // 2. Cargar Ciudades
    this.userService.getCities().subscribe({
      next: (res) => this.availableCities.set(res.data)
    });
  }

  // --- MÉTODOS DE EDICIÓN EN LÍNEA ---
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

    const updateData: UpdateUserRequest = { [field]: this.tempValue };

    this.userService.updateProfile(updateData).subscribe({
      next: (res) => {
        // Actualizamos la señal localmente
        const currentUser = this.usuario();
        if (currentUser) {
          (currentUser as any)[field] = this.tempValue;
          this.usuario.set({ ...currentUser }); // Forzamos refresco de señal
        }
        this.editingField.set(null);
        alert('Dato actualizado correctamente');
      },
      error: (err) => alert(err.error?.mensaje || 'Error al actualizar')
    });
  }

  // --- MÉTODOS DE SEGURIDAD ---
  changePassword() {
    if (this.passwordData.passwordNuevo !== this.passwordData.confirmacion) {
      alert('Las nuevas contraseñas no coinciden.');
      return;
    }

    this.isPasswordLoading.set(true);

    // Extraemos solo lo que el backend necesita
    const { confirmacion, ...requestData } = this.passwordData;

    this.userService.updatePassword(requestData).subscribe({
      next: (res) => {
        alert('Contraseña actualizada con éxito.');
        this.isPasswordLoading.set(false);
        // Limpiamos el formulario
        this.passwordData = { passwordActual: '', passwordNuevo: '', confirmacion: '' };
      },
      error: (err) => {
        alert(err.error?.mensaje || 'Error al cambiar contraseña');
        this.isPasswordLoading.set(false);
      }
    });
  }
}
