import { Component, OnInit, inject, signal } from '@angular/core';
import { ModalFeedbackComponent } from '../../../components/general/modal-feedback.component/modal-feedback.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user/user.service';
import { UserProfile, UpdatePasswordRequest } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { FotoPerfilService } from '../../../services/user/foto-perfil'; // Ajustá la ruta si es necesario
import { NgxDropzoneModule } from 'ngx-dropzone'; // Si tu componente es standalone
import { HttpClient } from '@angular/common/http';

interface Barrio {
  nombre: string;
  lat: number;
  lon: number;
}

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
  private http = inject(HttpClient);

  public icons = UI_ICONS;

  public usuario = signal<UserProfile | null>(null);
  public isLoading = signal(true);

  // --- LÓGICA DE FOTO DE PERFIL ---
  public files: File[] = []; //
  public isPhotoLoading = signal(false);
  public isEditingPhoto = signal(false); // Controla si mostramos el dropzone

  // --- LÓGICA DE EDICIÓN (DATOS PERSONALES) ---
  // --- VARIABLES PARA BARRIOS (JSON) ---
  public allBarrios: Barrio[] = [];
  public citySuggestions = signal<any[]>([]);

  public editingField = signal<string | null>(null);
  public tempValue = '';

  public tempLat: number | null = null;
  public tempLon: number | null = null;

  public passwordData: UpdatePasswordRequest & { confirmacion: string } = {
    passwordActual: '',
    passwordNuevo: '',
    confirmacion: ''
  };
  public isPasswordLoading = signal(false);

  public showCurrentPass = signal(false);
  public showNewPass = signal(false);
  public showConfirmPass = signal(false);

  public feedbackData = { visible: false, tipo: 'success' as 'success' | 'error', titulo: '', mensaje: '' };

  mostrarFeedback(titulo: string, mensaje: string, tipo: 'success' | 'error' = 'success') {
    this.feedbackData = { visible: true, titulo, mensaje, tipo };
  }

  cerrarFeedback() {
    this.feedbackData = { ...this.feedbackData, visible: false };
  }

  ngOnInit() {
    this.loadData();
    this.cargarBarriosDelBackend();
  }

  cargarBarriosDelBackend() {
    this.http.get<Barrio[]>('http://localhost:8080/api/barrios?ciudad=mdp')
      .subscribe({
        next: (data) => {
          this.allBarrios = data;
        },
        error: (err) => console.error('Error cargando barrios en perfil:', err)
      });
  }

  loadData() {
    this.isLoading.set(true);
    this.userService.getProfile().subscribe({
      next: (res) => {
        this.usuario.set(res.data);
        this.isLoading.set(false);
      },
      error: (err) => { console.error(err); this.isLoading.set(false); }
    });
  }

  // --- NUEVOS MÉTODOS PARA LA FOTO ---
  onSelect(event: any) {
    this.files = [];
    this.files.push(...event.addedFiles);
  }

  onRemove(event: any) {
    this.files.splice(this.files.indexOf(event), 1);
  }

  guardarFoto() {
    const user = this.usuario();
    if (user && this.files.length > 0) {
      this.isPhotoLoading.set(true);

      this.fotoService.subirFoto(this.files[0], (user as any).id || user.usuarioId).subscribe({
        next: (res) => {
          // Actualizamos la señal del usuario localmente para que cambie la foto en la vista
          this.usuario.set({ ...user, fotoUrl: res.url });
          this.isPhotoLoading.set(false);
          this.files = [];
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
    this.citySuggestions.set([]);
    this.tempLat = null;
    this.tempLon = null;
  }

  cancelEdit() {
    this.editingField.set(null);
    this.tempValue = '';
    this.citySuggestions.set([]);
  }

  buscarCiudades(event: Event) {
    const input = event.target as HTMLInputElement;
    const termino = input.value.toLowerCase();
    this.tempValue = input.value;

    if (termino.length < 1) {
      this.citySuggestions.set([]);
      return;
    }

    const filtrados = this.allBarrios
      .filter(b => b.nombre.toLowerCase().includes(termino))
      .slice(0, 5);

    this.citySuggestions.set(filtrados.map(b => ({
      nombreVisual: b.nombre,
      lat: b.lat,
      lon: b.lon
    })));
  }

  seleccionarCiudad(sugerencia: any) {
    this.tempValue = sugerencia.nombreVisual;
    this.tempLat = sugerencia.lat;
    this.tempLon = sugerencia.lon;
    this.citySuggestions.set([]);
  }

  saveEdit(field: string) {
    if (!this.tempValue.trim()) return;

    let updateData: any = { [field]: this.tempValue };

    if (field === 'ciudad') {
      if (this.tempLat && this.tempLon) {
        updateData.latitud = this.tempLat;
        updateData.longitud = this.tempLon;
      }
    }

    this.userService.updateProfile(updateData).subscribe({
      next: (res) => {
        const currentUser = this.usuario();
        if (currentUser) {
          (currentUser as any)[field] = this.tempValue;

          if (field === 'ciudad' && updateData.latitud) {
            (currentUser as any).latitud = updateData.latitud;
            (currentUser as any).longitud = updateData.longitud;
          }
          this.usuario.set({ ...currentUser });
        }
        this.editingField.set(null);

        // MENSAJE PERSONALIZADO
        const mensaje = field === 'ciudad' ? 'Barrio actualizado correctamente' : 'Dato actualizado correctamente';
        this.mostrarFeedback('¡Actualizado!', mensaje);
      },
      error: (err) => this.mostrarFeedback('Error', err.error?.mensaje || 'Error al actualizar', 'error')
    });
  }

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
