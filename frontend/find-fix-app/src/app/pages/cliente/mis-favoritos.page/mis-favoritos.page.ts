import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Modelos
import { FavoritoModel } from '../../../models/favoritos/lista-favs.model';
import { PerfilEspecialista } from '../../../models/especialista/especialista.model';

// Servicios
import { FavoritoService } from '../../../services/favoritos/lista-favs.service';
import { BuscarEspecialistaService } from '../../../services/cliente/buscar-especialista-service';
import { ModalConfirmacionComponent } from '../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component';
import { ModalFeedbackComponent } from "../../../components/general/modal-feedback.component/modal-feedback.component";

@Component({
  selector: 'app-mis-favoritos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ModalConfirmacionComponent, ModalFeedbackComponent],
  templateUrl: './mis-favoritos.page.html',
  styleUrls: ['./mis-favoritos.page.css']
})
export class MisFavoritosPage implements OnInit {

  private favoritoService = inject(FavoritoService);
  private clienteService = inject(BuscarEspecialistaService); // Reutilizamos este servicio para ver perfil/contratar

  // Datos de la lista
  favoritos = signal<FavoritoModel[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  // --- Lógica de Modales (Igual que en BuscarEspecialistas) ---
  public showModalContratar = signal(false);
  public showModalDetalle = signal(false);

  // Para el modal de detalle (perfil completo)
  public especialistaSeleccionaCompleto = signal<PerfilEspecialista | null>(null);
  public isLoadingDetalle = signal(false);

  // Para el modal de contratar
  public especialistaParaContratar: { nombre: string, email: string } | null = null;
  public descripcionTrabajo = '';
  public isSubmitting = signal(false);

  //modal confirmacion
  public showModalConfirmacion = signal(false);
  public emailParaEliminar = signal<string | null>(null);

  // Feedback modal state
  public feedbackData = { visible: false, tipo: 'success' as 'success' | 'error', titulo: '', mensaje: '' };

  mostrarFeedback(titulo: string, mensaje: string, tipo: 'success' | 'error' = 'success') {
    this.feedbackData = { visible: true, titulo, mensaje, tipo };
  }

  cerrarFeedback() {
    this.feedbackData = { ...this.feedbackData, visible: false };
  }
  ngOnInit(): void {
    this.cargarFavoritos();
  }

  cargarFavoritos(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.favoritoService.obtenerFavoritosPorCliente().subscribe({
      next: (response) => {
        console.log('Favoritos recibidos:', response.data);
        this.favoritos.set(response.data || []);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar favoritos:', err);
        this.errorMessage.set('No se pudo cargar la lista. Por favor, verifica tu sesión.');
        this.isLoading.set(false);
      }
    });
  }

  // --- Lógica Ver Perfil (Reutilizada) ---
  abrirModalDetalle(email: string) {
    this.especialistaSeleccionaCompleto.set(null);
    this.isLoadingDetalle.set(true);
    this.showModalDetalle.set(true);

    this.clienteService.obtenerPerfilCompleto(email).subscribe({
      next: (res) => {
        this.especialistaSeleccionaCompleto.set(res.data);
        this.isLoadingDetalle.set(false);
      },
      error: (err) => {
        console.error(err);
        this.isLoadingDetalle.set(false);
        this.cerrarModalDetalle();
        alert("No se pudo cargar el detalle del especialista.");
      }
    });
  }

  cerrarModalDetalle() {
    this.showModalDetalle.set(false);
  }

  // --- Lógica Contratar (Desde el modal de perfil) ---
  abrirModalContratar(especialista: PerfilEspecialista | null) {
    if (!especialista) return;
    this.especialistaParaContratar = {
      nombre: especialista.nombre + ' ' + especialista.apellido,
      email: especialista.email
    };
    this.descripcionTrabajo = '';
    this.showModalContratar.set(true);
  }

  cerrarModalContratar() {
    this.showModalContratar.set(false);
    this.especialistaParaContratar = null;
  }

  enviarSolicitud() {
    if (!this.especialistaParaContratar || !this.descripcionTrabajo.trim()) return;

    this.isSubmitting.set(true);

    this.clienteService.contratarEspecialista({
      emailEspecialista: this.especialistaParaContratar.email,
      descripcion: this.descripcionTrabajo
    }).subscribe({
      next: () => {
        alert('Solicitud enviada con éxito!');
        this.isSubmitting.set(false);
        this.cerrarModalContratar();
        this.cerrarModalDetalle();
      },
      error: (err) => {
        alert('Error al enviar solicitud: ' + (err.error?.mensaje || 'Intente nuevamente'));
        this.isSubmitting.set(false);
      }
    });
  }

  // --- Lógica Eliminar Favorito ---
  eliminarDeFavoritos(email: string, event: Event): void {
    event.stopPropagation();
    this.emailParaEliminar.set(email);
    this.showModalConfirmacion.set(true);
  }

  confirmarEliminacion(): void {
    const email = this.emailParaEliminar();
    if (!email) return;

    this.favoritoService.eliminarFavorito(email).subscribe({
      next: () => {
        this.favoritos.update(lista => lista.filter(fav => fav.email !== email));
        this.cerrarModalConfirmacion();
        this.mostrarFeedback('Eliminado', 'Se ha quitado al especialista de su lista');
      },
      error: (err) => {
        console.error('Error al eliminar favorito:', err);
        alert('Error al eliminar.');
        this.cerrarModalConfirmacion();
      }
    });
  }

  cerrarModalConfirmacion(): void {
    this.showModalConfirmacion.set(false);
    this.emailParaEliminar.set(null);
  }
}
