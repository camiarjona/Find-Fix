import { Component, inject, signal } from '@angular/core';
import { FiltroEspecialistasDTO, EspecialistaDTO } from '../../../models/cliente/buscar-especialistas-models';
import { BuscarEspecialistaService } from '../../../services/cliente/buscar-especialista-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-buscar-especialistas-component',
  imports: [CommonModule,FormsModule],
  templateUrl: './buscar-especialistas-component.html',
  styleUrl: './buscar-especialistas-component.css',
})
export class BuscarEspecialistasComponent {
private clienteService = inject(BuscarEspecialistaService);

  // Datos desde el servicio
  public especialistas = this.clienteService.especialistas;
  public ciudades = this.clienteService.ciudades;
  public oficiosDisponibles = this.clienteService.oficios;

  // Estado de Filtros
  public filtros: FiltroEspecialistasDTO = {
    ciudad: '',
    oficio: '',
    minCalificacion: 0 // 0 significa todos
  };

  // Estado del Modal de Contratación
  public showModalContratar = signal(false);
  public especialistaSeleccionado: EspecialistaDTO | null = null;
  public descripcionTrabajo = '';
  public isSubmitting = signal(false);

  ngOnInit() {
    this.clienteService.obtenerEspecialistas();
    this.clienteService.cargarDatosFiltros();
  }

  // --- Lógica de Filtros ---
  aplicarFiltros() {
    // Limpiamos valores vacíos para no enviarlos
    const filtrosEnviar: FiltroEspecialistasDTO = {};
    if (this.filtros.ciudad) filtrosEnviar.ciudad = this.filtros.ciudad;
    if (this.filtros.oficio) filtrosEnviar.oficio = this.filtros.oficio;
    if (this.filtros.minCalificacion && this.filtros.minCalificacion > 0) {
       filtrosEnviar.minCalificacion = this.filtros.minCalificacion;
    }

    // Si no hay filtros, recargamos todos. Si hay, filtramos.
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

  // --- Lógica de Contratación (Modal) ---
  abrirModalContratar(especialista: EspecialistaDTO) {
    this.especialistaSeleccionado = especialista;
    this.descripcionTrabajo = '';
    this.showModalContratar.set(true);
  }

  cerrarModal() {
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
        alert('Solicitud enviada con éxito!');
        this.isSubmitting.set(false);
        this.cerrarModal();
      },
      error: (err) => {
        alert('Error al enviar solicitud: ' + (err.error?.mensaje || 'Intente nuevamente'));
        this.isSubmitting.set(false);
      }
    });
  }

  // --- Favoritos (Placeholder) ---
  toggleFavorito(esp: EspecialistaDTO) {
    alert('La funcionalidad de Favoritos estará disponible próximamente.');
  }
}

