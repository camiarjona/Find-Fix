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
    this.cd.detectChanges();
  }

  cerrarFeedback() {
    this.feedbackData = { ...this.feedbackData, visible: false };
    this.cd.detectChanges();
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

  // --- Lógica de Foto de Perfil ---
  onSelect(event: any) {
    if (event.addedFiles && event.addedFiles.length > 0) {
      this.files = [];
      const file = event.addedFiles[0];

      setTimeout(() => {
        this.files = [file];

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
    this.tempValue = currentValue ? String(currentValue) : '';
    this.citySuggestions.set([]);
    this.tempLat = null;
    this.tempLon = null;
  }

  cancelEdit() {
    this.editingField.set(null);
    this.tempValue = '';
    this.citySuggestions.set([]);
  }

  // --- Sanitizadores y Validaciones del Perfil del Cliente ---
  soloNumerosKeydown(event: KeyboardEvent) {
    const teclasPermitidas = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Home', 'End'];
    if (teclasPermitidas.includes(event.key)) return;

    if (!/^\d$/.test(event.key)) {
      event.preventDefault();
    }
  }

  onInputSoloNumeros(event: Event) {
    const input = event.target as HTMLInputElement;
    const limpio = input.value.replace(/\D/g, '');
    this.tempValue = limpio;
    input.value = limpio;
  }

  soloLetrasKeydown(event: KeyboardEvent) {
    const teclasPermitidas = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Home', 'End', ' '];
    if (teclasPermitidas.includes(event.key)) return;

    if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ]$/.test(event.key)) {
      event.preventDefault();
    }
  }

  private validarCampoPerfil(field: string, valor: string): { valido: boolean; mensaje: string } {
    const val = valor.trim();

    switch (field) {
      case 'nombre':
      case 'apellido':
        if (val.length < 2 || val.length > 50) {
          return { valido: false, mensaje: 'El nombre/apellido debe contener entre 2 y 50 caracteres.' };
        }
        break;

      case 'telefono':
        if (!/^\d{8,15}$/.test(val)) {
          return { valido: false, mensaje: 'El teléfono debe contener entre 8 y 15 dígitos numéricos.' };
        }
        break;

      case 'ciudad':
        if (val.length < 3) {
          return { valido: false, mensaje: 'La zona de trabajo debe contener al menos 3 caracteres.' };
        }
        break;
    }

    return { valido: true, mensaje: '' };
  }

  buscarCiudades(event: Event) {
    const input = event.target as HTMLInputElement;
    const limpio = input.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');

    input.value = limpio;
    this.tempValue = limpio;
    const termino = limpio.toLowerCase().trim();

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
    const valorTexto = String(this.tempValue || '').trim();

    if (!valorTexto) {
      this.mostrarFeedback('Campo requerido', 'Este campo no puede estar vacío.', 'error');
      return;
    }

    const validacion = this.validarCampoPerfil(field, valorTexto);
    if (!validacion.valido) {
      this.mostrarFeedback('Dato inválido', validacion.mensaje, 'error');
      return;
    }

    let updateData: any = { [field]: valorTexto };

    if (field === 'ciudad' && this.tempLat && this.tempLon) {
      updateData.latitud = this.tempLat;
      updateData.longitud = this.tempLon;
    }

    this.userService.updateProfile(updateData).subscribe({
      next: (res) => {
        const currentUser = this.usuario();
        if (currentUser) {
          (currentUser as any)[field] = valorTexto;
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

      const coords = await this.locationService.obtenerCoordenadasGPS();

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

      setTimeout(() => {
        this.cerrarFeedback();
      }, 600);

    } catch (err) {
      this.mostrarFeedback('Error', 'No se pudo acceder al GPS', 'error');
    }
  }
}
