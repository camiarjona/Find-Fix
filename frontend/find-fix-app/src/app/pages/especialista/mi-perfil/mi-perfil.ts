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

@Component({
  selector: 'app-mi-perfil',
  imports: [CommonModule, FormsModule, ModalFeedbackComponent, ModalConfirmacionComponent],
  templateUrl: './mi-perfil.html',
  styleUrl: './mi-perfil.css',
})
export class MiPerfilEspecialista implements OnInit {

  private especialistaService = inject(EspecialistaService);
  private userService = inject(UserService);
  private oficiosService = inject(OficiosService);

  public icons = UI_ICONS;

  // --- ESTADO ---
  public perfil = signal<PerfilEspecialista | null>(null);
  public allOficios = signal<OficioModel[]>([]); // Lista para el select
  public selectableOficios = signal<OficioModel[]>([]); // Oficios disponibles para agregarse
  public availableCities = signal<string[]>([]);
  public isLoading = signal(true);

  // --- EDICIÓN SIMPLE ---
  public editingField = signal<string | null>(null);
  public tempValue = '';

  // --- GESTIÓN DE OFICIOS ---
  public isAddingOficio = signal(false);
  public selectedOficioToAdd = '';
  // Estado para controlar el modal de confirmación al eliminar un oficio
  public oficioToRemove: string | null = null;

  // --- SEGURIDAD ---
  public passwordData = { passwordActual: '', passwordNuevo: '', confirmacion: '' };
  public isPasswordLoading = signal(false);

  // Visibilidad Password
  public showCurrentPass = signal(false);
  public showNewPass = signal(false);
  public showConfirmPass = signal(false);

  // ESTADO DEL MODAL DE FEEDBACK
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
    this.especialistaService.getMiPerfil().subscribe({
      next: (data) => {
        this.perfil.set(data);
        this.isLoading.set(false);
        this.updateSelectableOficios();
      },
      error: (err) => { console.error(err); this.isLoading.set(false); }
    });

    // 2. Cargar Ciudades
    this.userService.getCities().subscribe(res => this.availableCities.set(res.data));

    // 3. Cargar Oficios
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
    if (!all || all.length === 0) {
      this.selectableOficios.set([]);
      return;
    }

    if (!perfil || !perfil.oficios) {
      this.selectableOficios.set(all);
      return;
    }

    const existentes = new Set(perfil.oficios.map(o => String(o.nombre)));
    const disponibles = all.filter(o => !existentes.has(String(o.nombre)));
    this.selectableOficios.set(disponibles);
  }

  // --- MÉTODOS DE EDICIÓN ---
  startEdit(field: string, value: any) {
    this.editingField.set(field);
    this.tempValue = value || '';
  }

  cancelEdit() {
    this.editingField.set(null);
    this.tempValue = '';
  }

  saveEdit(field: string) {
    const valorTexto = String(this.tempValue);
    if (!valorTexto.trim()) return;

    const data: Partial<PerfilEspecialista> = { [field]: this.tempValue };

    this.especialistaService.actualizarDatos(data).subscribe({
      next: () => {
        this.perfil.update(p => p ? { ...p, [field]: this.tempValue } : null);
        this.editingField.set(null);
        this.mostrarFeedback('¡Actualizado!', `Tu ${field} se ha guardado correctamente.`);
      },
      error: (err) => {
        this.mostrarFeedback('Error', 'No se pudieron guardar los cambios.', 'error');
      }
    });
  }

  // --- MÉTODOS DE OFICIOS ---
  addOficio() {
    if (!this.selectedOficioToAdd) return;
    const perfil = this.perfil();
    if (perfil && perfil.oficios && perfil.oficios.some(o => o.nombre === this.selectedOficioToAdd)) {
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
      error: (err) => {
        if (err && err.status === 409) {
          const msg = err.error?.mensaje || 'Ya existe ese oficio en tu perfil.';
          this.mostrarFeedback('Error', msg, 'error');
        } else {
          this.mostrarFeedback('Error', 'No se pudieron guardar los cambios.', 'error');
        }
      }
    });
  }

  removeOficio(nombreOficio: string) {
    const dto: ActualizarOficios = { agregar: [], eliminar: [nombreOficio] };

    this.especialistaService.actualizarOficios(dto).subscribe({
      next: () => {
        this.perfil.update(p => p ? { ...p, oficios: p.oficios.filter(o => o.nombre !== nombreOficio) } : null);
        this.updateSelectableOficios();
      },
      error: (err) => {
        this.mostrarFeedback('Error', 'Error al eliminar oficio', 'error');
      }
    });
  }

  promptRemoveOficio(nombreOficio: string) {
    this.oficioToRemove = nombreOficio;
  }

  cancelRemoveOficio() {
    this.oficioToRemove = null;
  }

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
        this.mostrarFeedback('Error', err?.error?.mensaje || 'No se pudo eliminar el oficio.', 'error');
        this.oficioToRemove = null;
      }
    });
  }

  reloadProfile() {
    this.especialistaService.getMiPerfil().subscribe(data => {
      this.perfil.set(data);
      this.updateSelectableOficios();
    });
  }

  // --- SEGURIDAD ---
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
