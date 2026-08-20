import { ModalFeedbackComponent } from './../../../components/general/modal-feedback.component/modal-feedback.component';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, ChangeDetectorRef } from '@angular/core';
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
import { LocationService } from '../../../services/general/location.service';
import { FotoTrabajoService } from '../../../services/especialista/foto-trabajo';

interface Barrio {
  nombre: string;
  lat: number;
  lon: number;
}

@Component({
  selector: 'app-mi-perfil',
  imports: [CommonModule, FormsModule, ModalFeedbackComponent, ModalConfirmacionComponent, NgxDropzoneModule],
  standalone: true,
  templateUrl: './mi-perfil.html',
  styleUrl: './mi-perfil.css',
})
export class MiPerfilEspecialista implements OnInit {

  private especialistaService = inject(EspecialistaService);
  private userService = inject(UserService);
  private oficiosService = inject(OficiosService);
  private http = inject(HttpClient);
  private cd = inject(ChangeDetectorRef);
  private locationService = inject(LocationService);
  private fotoService = inject(FotoPerfilService);
  private fotoTrabajoService = inject(FotoTrabajoService);

  public icons = UI_ICONS;

  public perfil = signal<PerfilEspecialista | null>(null);
  public perfilEspecialista = this.perfil;
  public allOficios = signal<OficioModel[]>([]);
  public selectableOficios = signal<OficioModel[]>([]);
  public citySuggestions = signal<any[]>([]);
  public isLoading = signal(true);

  public isEditingPhoto = signal(false);
  public isPhotoLoading = signal(false);
  public tempPhotoUrl = signal<string | null>(null);
  public files: File[] = [];
  public fotoError = signal(false);
  public galeriaFiles = signal<File[]>([]);
  public isGaleriaLoading = signal(false);
  public galeriaPreviews = signal<{ id: string, url: string, file: File }[]>([]);

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
  public hasTyped: boolean = false;

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

  // --- Lógica de Foto de Perfil ---
  onSelect(event: any) {
    if (event.addedFiles && event.addedFiles.length > 0) {
      const file: File = event.addedFiles[0];

      const reader = new FileReader();
      reader.onload = (e: any) => {
        const img = new Image();
        img.src = e.target.result;

        img.onload = () => {
          // 🛑 VALIDACIÓN DE DIMENSIONES MÁXIMAS (1920x1080)
          if (img.width > 1920 || img.height > 1080) {
            this.files = [];
            this.tempPhotoUrl.set(null);

            this.mostrarFeedback(
              'Resolución no permitida',
              `La foto de perfil supera la resolución máxima permitida de 1920x1080px (Tu foto mide ${img.width}x${img.height}px). Por favor, selecciona una más pequeña.`,
              'error'
            );
            return;
          }

          // ✅ Si cumple, asignamos la imagen
          this.files = [file];
          this.tempPhotoUrl.set(e.target.result);
          this.cd.detectChanges();
        };
      };

      reader.readAsDataURL(file);
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
        this.isPhotoLoading.set(false);
        console.error('Error en la subida:', err);

        if (err.status === 400 && err.error?.error === 'IMAGEN_INAPROPIADA') {
          this.files = [];
          this.mostrarFeedback(
            'Imagen rechazada',
            'La IA ha detectado contenido inapropiado. Por favor, elige otra foto.',
            'error'
          );
        } else {
          this.mostrarFeedback('Error', 'Falló la subida a Cloudinary', 'error');
        }
      }
    });
  }

  eliminarFotoActual() {
    const perfilActual = this.perfil();
    if (!perfilActual) return;

    const idFinal = perfilActual.id || (perfilActual as any).usuarioId;

    if (confirm('¿Estás seguro de que querés eliminar tu foto de perfil?')) {
      this.isPhotoLoading.set(true);

      this.fotoService.eliminarFoto(idFinal.toString()).subscribe({
        next: () => {
          this.perfil.update(p => p ? { ...p, fotoUrl: '' } : null);
          this.isPhotoLoading.set(false);
          this.cancelarCambioFoto();
          this.mostrarFeedback('¡Listo!', 'Foto eliminada correctamente.');
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          this.isPhotoLoading.set(false);
          this.mostrarFeedback('Error', 'No se pudo eliminar la foto del servidor.', 'error');
        }
      });
    }
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
    this.tempValue = value ? String(value) : '';
    this.citySuggestions.set([]);
    this.tempLat = null;
    this.tempLon = null;
  }

  cancelEdit() {
    this.editingField.set(null);
    this.tempValue = '';
    this.citySuggestions.set([]);
  }


  // --- Métodos Sanitizadores y Validaciones de Perfil ---

  soloNumerosKeydown(event: KeyboardEvent) {
    const teclasPermitidas = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Home', 'End'];
    if (teclasPermitidas.includes(event.key)) return;

    if (!/^\d$/.test(event.key)) {
      event.preventDefault();
    }
  }

  soloNumerosInput(event: Event) {
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
      case 'dni':
        if (!/^\d{7,8}$/.test(val)) {
          return { valido: false, mensaje: 'El DNI debe tener entre 7 y 8 números, sin puntos ni espacios.' };
        }
        break;

      case 'telefono':
        if (!/^\d{10}$/.test(val)) {
          return { valido: false, mensaje: 'El teléfono debe contener exactamente 10 dígitos numéricos.' };
        }
        break;

      case 'descripcion':
        if (val.length < 10) {
          return { valido: false, mensaje: 'La descripción debe tener al menos 10 caracteres.' };
        }
        if (val.length > 500) {
          return { valido: false, mensaje: 'La descripción no puede superar los 500 caracteres.' };
        }
        break;

      case 'ciudad':
        if (val.length < 3) {
          return { valido: false, mensaje: 'La zona de trabajo debe contener al menos 3 caracteres.' };
        }

        const barrioValido = this.allBarrios.some(
          (b: any) => b.nombre.toLowerCase() === val.trim().toLowerCase()
        );

        if (!barrioValido) {
          return {
            valido: false,
            mensaje: 'El barrio ingresado no se encuentra en la lista oficial. Por favor, selecciona uno válido de las sugerencias.'
          };
        }
        break;
    }

    return { valido: true, mensaje: '' };
  }

 buscarZona(event: Event) {
  const input = event.target as HTMLInputElement;

  // Elimina números y símbolos, dejando solo letras (con acentos/ñ) y espacios
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

  seleccionarZona(sugerencia: any) {
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

    const data: any = { [field]: valorTexto };

    if (field === 'ciudad' && this.tempLat && this.tempLon) {
      data.latitud = this.tempLat;
      data.longitud = this.tempLon;
    }

    this.especialistaService.actualizarDatos(data).subscribe({
      next: () => {
        this.perfil.update(p => {
          if (!p) return null;
          const updated = { ...p, [field]: valorTexto };

          if (field === 'ciudad' && this.tempLat && this.tempLon) {
            (updated as any).latitud = this.tempLat;
            (updated as any).longitud = this.tempLon;
          }
          return updated;
        });

        this.editingField.set(null);
        const nombreCampo = field === 'ciudad' ? 'Barrio' : field;
        this.mostrarFeedback('¡Actualizado!', `Tu ${nombreCampo} se guardó correctamente.`);
      },
      error: (err) => {
        this.mostrarFeedback('Error', err?.error?.mensaje || 'No se pudieron guardar los cambios.', 'error');
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
      this.mostrarFeedback('Error', 'Las contraseñas no coinciden', 'error');
      return;
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

  async detectarYGuardarZona() {
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
      console.error(err);
      this.mostrarFeedback('Error', 'No se pudo obtener la ubicación', 'error');
    }
  }

  // --- Lógica de Galería ---
  onSelectGaleria(event: any) {
    const archivosActuales = this.galeriaFiles();
    const nuevosArchivos: File[] = event.addedFiles;

    if (archivosActuales.length + nuevosArchivos.length > 5) {
      this.mostrarFeedback('Límite excedido', 'Solo podés subir hasta 5 fotos para tu galería simultáneamente.', 'error');
      return;
    }

    nuevosArchivos.forEach((file: File) => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const img = new Image();
        img.src = e.target.result;

        img.onload = () => {
          // 🛑 VALIDACIÓN DE DIMENSIONES MÁXIMAS (1920x1080)
          if (img.width > 1920 || img.height > 1080) {
            this.mostrarFeedback(
              'Resolución no permitida',
              `La imagen "${file.name}" supera los 1920x1080px (${img.width}x${img.height}px). No fue agregada.`,
              'error'
            );
            return;
          }

          // ✅ Si cumple, agregamos el archivo y creamos su preview
          this.galeriaFiles.set([...this.galeriaFiles(), file]);

          const nuevoPreview = {
            id: Math.random().toString(36).substring(2),
            url: URL.createObjectURL(file),
            file: file
          };

          this.galeriaPreviews.set([...this.galeriaPreviews(), nuevoPreview]);
          this.cd.detectChanges();
        };
      };

      reader.readAsDataURL(file);
    });
  }

  onRemoveGaleria(idPreview: string) {
    const itemABorrar = this.galeriaPreviews().find(p => p.id === idPreview);

    if (itemABorrar) {
      URL.revokeObjectURL(itemABorrar.url);

      this.galeriaPreviews.set(this.galeriaPreviews().filter(p => p.id !== idPreview));
      this.galeriaFiles.set(this.galeriaFiles().filter(f => f !== itemABorrar.file));
    }

    this.cd.detectChanges();
  }

  guardarGaleria() {
    const archivos = this.galeriaFiles();
    if (archivos.length === 0) return;

    this.isGaleriaLoading.set(true);
    this.mostrarFeedback('Subiendo...', 'Estamos procesando y optimizando tus fotos en la nube...', 'success');

    this.fotoTrabajoService.subirFotos(archivos).subscribe({
      next: (res) => {
        this.isGaleriaLoading.set(false);

        this.galeriaPreviews().forEach(p => URL.revokeObjectURL(p.url));

        this.galeriaFiles.set([]);
        this.galeriaPreviews.set([]);

        this.mostrarFeedback('¡Éxito!', 'Tu galería de trabajos anteriores fue actualizada correctamente.');
        this.reloadProfile();
      },
      error: (err) => {
        this.isGaleriaLoading.set(false);
        console.error('Error al subir la galería:', err);
        this.mostrarFeedback('Error', 'No se pudieron subir las fotos de tus trabajos.', 'error');
      }
    });
  }

  eliminarFotoPersistida(idFoto: number): void {
    if (confirm('¿Estás seguro de que querés eliminar esta foto de tu galería de forma permanente?')) {
      this.fotoTrabajoService.eliminarFoto(idFoto).subscribe({
        next: (response) => {
          this.mostrarFeedback('¡Éxito!', 'La foto fue borrada de tu galería.');
          this.loadData();
        },
        error: (err) => {
          console.error('Error al intentar borrar la foto:', err);
          this.mostrarFeedback('Error', 'No se pudo eliminar la imagen.', 'error');
        }
      });
    }
  }

}
