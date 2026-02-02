import { Component, OnInit, inject, signal } from '@angular/core';
import { ModalFeedbackComponent } from '../../../components/general/modal-feedback.component/modal-feedback.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user/user.service';
import { UserProfile, UpdateUserRequest, UpdatePasswordRequest } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { FotoPerfilService } from '../../../services/user/foto-perfil'; // Ajustá la ruta si es necesario
import { NgxDropzoneModule } from 'ngx-dropzone'; // Si tu componente es standalone

@Component({
  selector: 'app-mi-perfil-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalFeedbackComponent, NgxDropzoneModule],
  templateUrl: './perfil.page.html',
  styleUrls: ['./perfil.page.css']
})
export class PerfilPage implements OnInit {

  private userService = inject(UserService);
  private fotoService = inject(FotoPerfilService);

  public icons = UI_ICONS;

  // --- ESTADO DE DATOS ---
  public usuario = signal<UserProfile | null>(null);
  public availableCities = signal<string[]>([]);
  public isLoading = signal(true);

  // --- LÓGICA DE FOTO DE PERFIL ---
  public files: File[] = []; //
  public isPhotoLoading = signal(false);
  public isEditingPhoto = signal(false); // Controla si mostramos el dropzone
  public tempPhotoUrl = signal<string | null>(null);

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

    // Visibilidad Password
  public showCurrentPass = signal(false);
  public showNewPass = signal(false);
  public showConfirmPass = signal(false);

  // Feedback modal state
  public feedbackData = { visible: false, tipo: 'success' as 'success' | 'error', titulo: '', mensaje: '' };

  mostrarFeedback(titulo: string, mensaje: string, tipo: 'success' | 'error' = 'success') {
    this.feedbackData = { visible: true, titulo, mensaje, tipo };
  }

  cerrarFeedback() {
    this.feedbackData = { ...this.feedbackData, visible: false };
  }

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

  // --- NUEVOS MÉTODOS PARA LA FOTO ---
  onSelect(event: any) {
  if (event.addedFiles && event.addedFiles.length > 0) {
    this.files = [event.addedFiles[0]]; // Solo permitimos uno, así que reemplazamos

    // CREAR PREVISUALIZACIÓN LOCAL INSTANTÁNEA
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.tempPhotoUrl.set(e.target.result); // Esto es instantáneo
    };
    reader.readAsDataURL(this.files[0]);
  }
}

 onRemove(event: any) {
  this.files = [];
  this.tempPhotoUrl.set(null);
}

  guardarFoto() {
  const user = this.usuario();
  if (user && this.files.length > 0) {
    this.isPhotoLoading.set(true);

    this.fotoService.subirFoto(this.files[0], user.usuarioId).subscribe({
      next: (res) => {
        this.usuario.set({ ...user, fotoUrl: res.url });
        this.isPhotoLoading.set(false);

        // --- MEJORAS ESTÉTICAS ---
        this.cancelarCambioFoto(); // Limpia archivos y cierra el modal
        this.tempPhotoUrl.set(null); // Limpia la previsualización temporal
        this.mostrarFeedback('¡Éxito!', 'Foto de perfil actualizada correctamente.');
      },
      error: (err) => {
        this.isPhotoLoading.set(false);
        this.mostrarFeedback('Error', 'No se pudo subir la foto.', 'error');
      }
    });
  }
}

  cancelarCambioFoto() {
  this.files = [];
  this.isEditingPhoto.set(false);
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
        this.mostrarFeedback('¡Actualizado!', 'Dato actualizado correctamente');
      },
      error: (err) => this.mostrarFeedback('Error', err.error?.mensaje || 'Error al actualizar', 'error')
    });
  }

  // --- MÉTODOS DE SEGURIDAD ---
    togglePass(field: 'curr' | 'new' | 'conf') {
    if (field === 'curr') this.showCurrentPass.update(v => !v);
    if (field === 'new') this.showNewPass.update(v => !v);
    if (field === 'conf') this.showConfirmPass.update(v => !v);
  }

  changePassword() {
    if (this.passwordData.passwordNuevo !== this.passwordData.confirmacion) {
      this.mostrarFeedback('Error', 'Las nuevas contraseñas no coinciden.', 'error');
      return;
    }

    this.isPasswordLoading.set(true);

    const { confirmacion, ...requestData } = this.passwordData;

    this.userService.updatePassword(requestData).subscribe({
      next: (res) => {
        this.mostrarFeedback('¡Actualizado!', 'Contraseña actualizada con éxito.');
        this.isPasswordLoading.set(false);
        this.passwordData = { passwordActual: '', passwordNuevo: '', confirmacion: '' };
      },
      error: (err) => {
        this.mostrarFeedback('Error', err.error?.mensaje || 'Error al cambiar contraseña', 'error');
        this.isPasswordLoading.set(false);
      }
    });
  }
}
