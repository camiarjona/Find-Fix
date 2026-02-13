import { ModalFeedbackComponent } from './../../../components/general/modal-feedback.component/modal-feedback.component';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EspecialistaService } from '../../../services/especialista/especialista.service';
import { UserService } from '../../../services/user/user.service';
import { OficiosService } from '../../../services/admin-services/oficios-service';
import { OficioModel } from '../../../models/admin-models/oficio-model';
import { ActualizarOficios, PerfilEspecialista } from '../../../models/especialista/especialista.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { ModalConfirmacionComponent } from '../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component';
import { HttpClient } from '@angular/common/http';
import { FotoPerfilService } from '../../../services/user/foto-perfil';
import { NgxDropzoneModule } from 'ngx-dropzone';

interface Barrio {
  nombre: string;
  lat: number;
  lon: number;
}

@Component({
  selector: 'app-mi-perfil',
  imports: [CommonModule, FormsModule, ModalFeedbackComponent, ModalConfirmacionComponent, NgxDropzoneModule],
  templateUrl: './mi-perfil.html',
  styleUrl: './mi-perfil.css',
})
export class MiPerfilEspecialista implements OnInit {

  private especialistaService = inject(EspecialistaService);
  private userService = inject(UserService);
  private oficiosService = inject(OficiosService);
  private http = inject(HttpClient);

  public icons = UI_ICONS;

  public perfil = signal<PerfilEspecialista | null>(null);
  public allOficios = signal<OficioModel[]>([]);
  public selectableOficios = signal<OficioModel[]>([]);
  public citySuggestions = signal<any[]>([]);
  public isLoading = signal(true);

  private fotoService = inject(FotoPerfilService);
  public isEditingPhoto = signal(false);
  public isPhotoLoading = signal(false);
  public tempPhotoUrl = signal<string | null>(null);
  public files: File[] = [];
  public fotoError = signal(false);

  public allBarrios: Barrio[] = [];

  public editingField = signal<string | null>(null);
  public tempValue = '';

  public tempLat: number | null = null;
  public tempLon: number | null = null;

  public isAddingOficio = signal(false);
  public selectedOficioToAdd = '';
  public oficioToRemove: string | null = null;

  public passwordData = { passwordActual: '', passwordNuevo: '', confirmacion: '' };
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

  // --- Lógica de Foto de Perfil ---
  onSelect(event: any) {
  console.log('Archivo seleccionado:', event.addedFiles);
  this.files = [...event.addedFiles]; // Usamos spread para asegurar la asignación

  if (this.files.length > 0) {
    const reader = new FileReader();
    reader.onload = () => {
      this.tempPhotoUrl.set(reader.result as string);
    };
    reader.readAsDataURL(this.files[0]);
  }
}

  onRemove(event: any) {
    this.files = [];
    this.tempPhotoUrl.set(null);
  }

  cancelarCambioFoto() {
    this.isEditingPhoto.set(false);
    this.files = [];
    this.tempPhotoUrl.set(null);
  }

  guardarFoto() {
  const perfilActual = this.perfil();
  if (!perfilActual) {
    console.error("El perfil es null, no se puede guardar la foto");
    return;
  }

  const idFinal = perfilActual.id || (perfilActual as any).usuarioId;

  if (this.files.length === 0 || !idFinal) {
    console.error("No se encontró archivo o ID. ID detectado:", idFinal);
    this.mostrarFeedback('Error', 'No se pudo identificar tu cuenta', 'error');
    return;
  }

  this.isPhotoLoading.set(true);

  this.fotoService.subirFoto(this.files[0], idFinal).subscribe({
    next: (res) => {
      this.perfil.update(p => p ? { ...p, fotoUrl: res.url } : null);
      this.mostrarFeedback('¡Éxito!', 'Foto actualizada.');
      this.cancelarCambioFoto();
      this.isPhotoLoading.set(false);
    },
    error: (err) => {
      console.error('Error en la subida:', err);
      this.mostrarFeedback('Error', 'Falló la subida a Cloudinary', 'error');
      this.isPhotoLoading.set(false);
    }
  });
}

  eliminarFotoActual() {
    this.perfil.update(p => p ? { ...p, fotoUrl: '' } : null);
    this.mostrarFeedback('Eliminada', 'Se ha quitado tu foto de perfil.');
    this.cancelarCambioFoto();
  }


  cargarBarriosDelBackend() {
    this.http.get<Barrio[]>('http://localhost:8080/api/barrios?ciudad=mdp')
      .subscribe({
        next: (data) => {
          this.allBarrios = data;
        },
        error: (err) => console.error('Error al cargar barrios del JSON', err)
      });
  }

  loadData() {
    this.isLoading.set(true);
    this.especialistaService.getMiPerfil().subscribe({
      next: (data) => {
        this.perfil.set(data);
        this.isLoading.set(false);
        this.updateSelectableOficios();
      },
      error: (err) => { console.error(err); this.isLoading.set(false); }
    });

    this.oficiosService.getOficios(false).subscribe({
      next: (res) => {
        const lista = 'data' in res ? res.data : res;
        this.allOficios.set(lista);
        this.updateSelectableOficios();
      },
      error: (err) => console.error('Error al cargar oficios', err)
    });
  }

  private updateSelectableOficios() {
    const all = this.allOficios();
    const perfil = this.perfil();
    if (!all || !all.length) { this.selectableOficios.set([]); return; }
    if (!perfil || !perfil.oficios) { this.selectableOficios.set(all); return; }
    const existentes = new Set(perfil.oficios.map(o => String(o.nombre)));
    this.selectableOficios.set(all.filter(o => !existentes.has(String(o.nombre))));
  }

  startEdit(field: string, value: any) {
    this.editingField.set(field);
    this.tempValue = value || '';
    this.citySuggestions.set([]);
    this.tempLat = null;
    this.tempLon = null;
  }

  cancelEdit() {
    this.editingField.set(null);
    this.tempValue = '';
    this.citySuggestions.set([]);
  }

  buscarZona(event: Event) {
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

  seleccionarZona(sugerencia: any) {
    this.tempValue = sugerencia.nombreVisual;
    this.tempLat = sugerencia.lat;
    this.tempLon = sugerencia.lon;
    this.citySuggestions.set([]);
  }

  saveEdit(field: string) {
    const valorTexto = String(this.tempValue);
    if (!valorTexto.trim()) return;

    const data: any = { [field]: this.tempValue };

    if (field === 'ciudad') {
      if (this.tempLat && this.tempLon) {
        data.latitud = this.tempLat;
        data.longitud = this.tempLon;
      }
    }

    this.especialistaService.actualizarDatos(data).subscribe({
      next: () => {
        this.perfil.update(p => {
          if (!p) return null;
          const updated = { ...p, [field]: this.tempValue };

          if (field === 'ciudad' && this.tempLat && this.tempLon) {
            (updated as any).latitud = this.tempLat;
            (updated as any).longitud = this.tempLon;
          }
          return updated;
        });

        this.editingField.set(null);

        const nombreCampo = field === 'ciudad' ? 'Barrio' : field;
        this.mostrarFeedback('¡Actualizado!', `Tu ${nombreCampo} se ha guardado correctamente.`);
      },
      error: (err) => {
        this.mostrarFeedback('Error', 'No se pudieron guardar los cambios.', 'error');
      }
    });
  }

  addOficio() {
    if (!this.selectedOficioToAdd) return;
    const perfil = this.perfil();
    if (perfil?.oficios?.some(o => o.nombre === this.selectedOficioToAdd)) {
      this.mostrarFeedback('Error', 'Ya tienes registrado ese oficio.', 'error');
      return;
    }
    const dto: ActualizarOficios = { agregar: [this.selectedOficioToAdd], eliminar: [] };
    this.especialistaService.actualizarOficios(dto).subscribe({
      next: () => {
        this.mostrarFeedback('¡Actualizado!', `Oficio agregado con éxito.`);
        this.isAddingOficio.set(false);
        this.selectedOficioToAdd = '';
        this.reloadProfile();
      },
      error: (err) => this.handleOficioError(err)
    });
  }

  promptRemoveOficio(nombreOficio: string) { this.oficioToRemove = nombreOficio; }
  cancelRemoveOficio() { this.oficioToRemove = null; }

  confirmRemoveOficio() {
    if (!this.oficioToRemove) return;
    const nombre = this.oficioToRemove;
    const dto: ActualizarOficios = { agregar: [], eliminar: [nombre] };
    this.especialistaService.actualizarOficios(dto).subscribe({
      next: () => {
        this.perfil.update(p => p ? { ...p, oficios: p.oficios.filter(o => o.nombre !== nombre) } : null);
        this.updateSelectableOficios();
        this.mostrarFeedback('¡Eliminado!', `Has dejado de ofrecer ${nombre}.`);
        this.oficioToRemove = null;
      },
      error: (err) => {
        this.mostrarFeedback('Error', err?.error?.mensaje || 'Error al eliminar', 'error');
        this.oficioToRemove = null;
      }
    });
  }

  handleOficioError(err: any) {
    if (err && err.status === 409) {
      this.mostrarFeedback('Error', err.error?.mensaje || 'Conflicto de oficios.', 'error');
    } else {
      this.mostrarFeedback('Error', 'Error al procesar solicitud.', 'error');
    }
  }

  reloadProfile() {
    this.especialistaService.getMiPerfil().subscribe(data => {
      this.perfil.set(data);
      this.updateSelectableOficios();
    });
  }

  togglePass(field: 'curr' | 'new' | 'conf') {
    if (field === 'curr') this.showCurrentPass.update(v => !v);
    if (field === 'new') this.showNewPass.update(v => !v);
    if (field === 'conf') this.showConfirmPass.update(v => !v);
  }

  changePassword() {
    if (this.passwordData.passwordNuevo !== this.passwordData.confirmacion) {
      alert('Las contraseñas no coinciden'); return;
    }
    this.isPasswordLoading.set(true);
    const { confirmacion, ...data } = this.passwordData;
    this.userService.updatePassword(data).subscribe({
      next: () => {
        this.mostrarFeedback('¡Actualizado!', 'Contraseña actualizada.');
        this.isPasswordLoading.set(false);
        this.passwordData = { passwordActual: '', passwordNuevo: '', confirmacion: '' };
      },
      error: (err) => {
        this.mostrarFeedback('Error', err.error?.mensaje || 'Error', 'error');
        this.isPasswordLoading.set(false);
      }
    });
  }
}
