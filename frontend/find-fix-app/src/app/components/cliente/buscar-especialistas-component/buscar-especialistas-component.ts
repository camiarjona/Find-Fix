import { Component, inject, signal } from '@angular/core';
import { FiltroEspecialistasDTO, EspecialistaDTO } from '../../../models/cliente/buscar-especialistas-models';
import { BuscarEspecialistaService } from '../../../services/cliente/buscar-especialista-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilEspecialista } from '../../../models/especialista/especialista.model';
import { FavoritoService } from '../../../services/favoritos/lista-favs.service';
import { AgregarFavoritoDTO } from '../../../models/favoritos/lista-favs.model';
import { ModalFeedbackComponent } from "../../general/modal-feedback.component/modal-feedback.component";

@Component({
  selector: 'app-buscar-especialistas-component',
  imports: [CommonModule, FormsModule, ModalFeedbackComponent],
  templateUrl: './buscar-especialistas-component.html',
  styleUrl: './buscar-especialistas-component.css',
})
export class BuscarEspecialistasComponent {
  private clienteService = inject(BuscarEspecialistaService);

  // Datos desde el servicio
  public especialistas = this.clienteService.especialistas;
  public ciudades = this.clienteService.ciudades;
  public oficiosDisponibles = this.clienteService.oficios;

  private favoritosService = inject(FavoritoService);

  public favoritosSet = signal<Set<string>>(new Set());

  // Estado de Filtros
  public filtros: FiltroEspecialistasDTO = {
    ciudad: '',
    oficio: '',
    minCalificacion: 0
  };

    public showConfirmPass = signal(false);

  // Feedback modal state
  public feedbackData = { visible: false, tipo: 'success' as 'success' | 'error', titulo: '', mensaje: '' };

  mostrarFeedback(titulo: string, mensaje: string, tipo: 'success' | 'error' = 'success') {
    this.feedbackData = { visible: true, titulo, mensaje, tipo };
  }

  cerrarFeedback() {
    this.feedbackData = { ...this.feedbackData, visible: false };
  }

  public showModalContratar = signal(false);
  public showModalDetalle = signal(false);
  public especialistaSeleccionado: EspecialistaDTO | any;
  public especialistaSeleccionaCompleto = signal<PerfilEspecialista | any>(null);
  public descripcionTrabajo = '';
  public isSubmitting = signal(false);
  public isLoadingDetalle = signal(false);

  ngOnInit() {
    this.clienteService.obtenerEspecialistas();
    this.clienteService.cargarDatosFiltros();
    this.cargarFavoritosDelUsuario();
  }

  cargarFavoritosDelUsuario() {
    this.favoritosService.obtenerFavoritosPorCliente().subscribe({
      next: (resp) => {
        const lista = resp.data || [];
        // Creamos un Set con los emails de los favoritos
        const emailsFavoritos = new Set(lista.map(fav => fav.email));
        this.favoritosSet.set(emailsFavoritos);
      },
      error: (err) => console.error('Error cargando favoritos iniciales', err)
    });
  }

  esFavorito(email: string): boolean {
    return this.favoritosSet().has(email);
  }

  aplicarFiltros() {
    const filtrosEnviar: FiltroEspecialistasDTO = {};
    if (this.filtros.ciudad) filtrosEnviar.ciudad = this.filtros.ciudad;
    if (this.filtros.oficio) filtrosEnviar.oficio = this.filtros.oficio;
    if (this.filtros.minCalificacion && this.filtros.minCalificacion > 0) {
      filtrosEnviar.minCalificacion = this.filtros.minCalificacion;
    }


    if (Object.keys(filtrosEnviar).length === 0) {
      this.clienteService.obtenerEspecialistas();
    } else {
      this.clienteService.filtrarEspecialistas(filtrosEnviar);
    }
  }

  limpiarFiltros() {
    this.filtros = { ciudad: '', oficio: '', minCalificacion: 0 };
    this.clienteService.obtenerEspecialistas();
  }


  abrirModalContratar(especialista: EspecialistaDTO) {
    this.especialistaSeleccionado = especialista;
    this.descripcionTrabajo = '';
    this.showModalContratar.set(true);
  }

  cerrarModalContratar() {
    this.showModalContratar.set(false);
    this.especialistaSeleccionado = null;
  }

  enviarSolicitud() {
    if (!this.especialistaSeleccionado || !this.descripcionTrabajo.trim()) return;

    this.isSubmitting.set(true);

    this.clienteService.contratarEspecialista({
      emailEspecialista: this.especialistaSeleccionado.email,
      descripcion: this.descripcionTrabajo
    }).subscribe({
      next: () => {
        this.mostrarFeedback('Enhorabuena', 'Solicitud enviada con éxito');
        this.isSubmitting.set(false);
        this.cerrarModalContratar();
      },
      error: (err) => {
        this.mostrarFeedback('Error al enviar la solicitud', 'Intente nuevamente');
        this.isSubmitting.set(false);
      }
    });
  }

  abrirModalDetalle(espListado: EspecialistaDTO) {
    // 2. Resetea el estado ANTES de abrir
    this.especialistaSeleccionaCompleto.set(null);
    this.isLoadingDetalle.set(true);
    this.showModalDetalle.set(true);

    this.clienteService.obtenerPerfilCompleto(espListado.email).subscribe({
      next: (res) => {
        console.log("Datos recibidos:", res.data); // Debug

        // 3. Actualiza la Signal con los datos
        this.especialistaSeleccionaCompleto.set(res.data);

        // 4. Apaga el loading AL FINAL
        this.isLoadingDetalle.set(false);
      },
      error: (err) => {
        console.error(err);
        this.isLoadingDetalle.set(false);
        this.cerrarModalDetalle();
        alert("No se pudo cargar el detalle.");
      }
    });
  }

  cerrarModalDetalle() {
    this.showModalDetalle.set(false);
  }

  toggleFavorito(esp: EspecialistaDTO | PerfilEspecialista, event?: Event) {
    if(event) event.stopPropagation();

    const email = esp.email;
    const yaEsFavorito = this.esFavorito(email);

    if (yaEsFavorito) {
      // --- ELIMINAR ---
      this.favoritosService.eliminarFavorito(email).subscribe({
        next: () => {
          this.favoritosSet.update(set => {
            const nuevoSet = new Set(set);
            nuevoSet.delete(email);
            return nuevoSet;
          });
        },
        error: (err) => alert('Error al quitar de favoritos')
      });

    } else {
      // --- AGREGAR ---
      const nuevoFavorito: AgregarFavoritoDTO = { especialistaEmail: email };

      this.favoritosService.agregarFavorito(nuevoFavorito).subscribe({
        next: () => {
          this.favoritosSet.update(set => {
            const nuevoSet = new Set(set);
            nuevoSet.add(email);
            return nuevoSet;
          });
        },
        error: (err) => alert('Error al agregar a favoritos')
      });
    }
  }
}

