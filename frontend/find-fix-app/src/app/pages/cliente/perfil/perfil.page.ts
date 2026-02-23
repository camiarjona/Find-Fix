import { Component, OnInit, inject, signal } from '@angular/core';
import { ModalFeedbackComponent } from '../../../components/general/modal-feedback.component/modal-feedback.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user/user.service';
import { UserProfile, UpdatePasswordRequest } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { FotoPerfilService } from '../../../services/user/foto-perfil';
import { NgxDropzoneModule } from 'ngx-dropzone';
import { ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LocationService } from '../../../services/general/location.service';

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
  private cd = inject(ChangeDetectorRef);
  private http = inject(HttpClient);
  private locationService = inject(LocationService);

  public icons = UI_ICONS;

  public usuario = signal<UserProfile | null>(null);
  public isLoading = signal(true);

  public files: File[] = [];
  public isPhotoLoading = signal(false);
  public isEditingPhoto = signal(false);
  public tempPhotoUrl = signal<string | null>(null);
  public fotoError = signal(false);

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
    this.cd.detectChanges(); // Forzamos que se vea
  }

  cerrarFeedback() {
    this.feedbackData = { ...this.feedbackData, visible: false };
    this.cd.detectChanges(); // Forzamos que se oculte
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
        this.fotoError.set(false);
        this.isLoading.set(false);
      },
      error: (err) => { console.error(err); this.isLoading.set(false); }
    });
  }

  // --- NUEVOS MÉTODOS PARA LA FOTO ---
  onSelect(event: any) {
    if (event.addedFiles && event.addedFiles.length > 0) {
      // 1. Limpiamos y creamos una referencia nueva
      // (Esto "resetea" visualmente el componente)
      this.files = [];

      const file = event.addedFiles[0];

      // 2. Usamos un pequeño delay (setTimeout) para que Angular
      // procese el vaciado antes de meter el nuevo archivo
      setTimeout(() => {
        this.files = [file]; // Asignamos el nuevo archivo en un array nuevo

        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.tempPhotoUrl.set(e.target.result);
          this.cd.detectChanges();
        };
        reader.readAsDataURL(file);

        this.cd.detectChanges();
      }, 0);
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
          this.cancelarCambioFoto();
          this.tempPhotoUrl.set(null);
          this.mostrarFeedback('¡Éxito!', 'Foto de perfil actualizada correctamente.');
        },
        error: (err) => {
          this.isPhotoLoading.set(false);

          if (err.status === 400 && err.error?.error === 'IMAGEN_INAPROPIADA') {
            this.files = [];
            this.mostrarFeedback(
              'Imagen rechazada',
              'La IA ha detectado contenido inapropiado. Por favor, elige otra foto.',
              'error'
            );
        } else {
          this.mostrarFeedback('Error', 'No se pudo subir la foto', 'error');
        }
        }
      });
    }
  }

  cancelarCambioFoto() {
    this.files = [];
    this.tempPhotoUrl.set(null);
    this.isEditingPhoto.set(false);
  }

  eliminarFotoActual() {
    const user = this.usuario();
    if (!user) return;
    if (confirm('¿Estás seguro de que querés eliminar tu foto de perfil?')) {
      this.isPhotoLoading.set(true);
      this.fotoService.eliminarFoto(user.usuarioId.toString()).subscribe({
        next: () => {
          this.usuario.set({ ...user, fotoUrl: undefined });
          this.isPhotoLoading.set(false);
          this.cancelarCambioFoto();
          this.mostrarFeedback('¡Listo!', 'Foto eliminada correctamente.');
        },
        error: (err) => {
          this.isPhotoLoading.set(false);
          this.mostrarFeedback('Error', 'No se pudo eliminar la foto.', 'error');
        }
      });
    }
  }

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

  async detectarYGuardarUbicacion() {
    try {
      this.mostrarFeedback('Ubicando...', 'Identificando tu barrio...', 'success');

      // 1. Buscamos coordenadas
      const coords = await this.locationService.obtenerCoordenadasGPS();

      // 2. Buscamos barrio
      const barrioEncontrado = this.locationService.obtenerBarrioMasCercano(
        coords.lat,
        coords.lon,
        this.allBarrios
      );

      if (barrioEncontrado) {
        this.tempLat = barrioEncontrado.lat;
        this.tempLon = barrioEncontrado.lon;
        this.tempValue = barrioEncontrado.nombre;
      }

      // 3. Pequeño delay y CIERRE FORZADO
      setTimeout(() => {
        this.cerrarFeedback();
      }, 600);

    } catch (err) {
      this.mostrarFeedback('Error', 'No se pudo acceder al GPS', 'error');
    }
  }
}
