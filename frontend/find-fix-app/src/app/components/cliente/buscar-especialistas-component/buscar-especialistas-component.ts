import { Component, inject, signal } from '@angular/core';
import { FiltroEspecialistasDTO, EspecialistaDTO } from '../../../models/cliente/buscar-especialistas-models';
import { BuscarEspecialistaService } from '../../../services/cliente/buscar-especialista-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilEspecialista } from '../../../models/especialista/especialista.model';

@Component({
  selector: 'app-buscar-especialistas-component',
  imports: [CommonModule, FormsModule],
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
    minCalificacion: 0
  };
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
        alert('Solicitud enviada con éxito!');
        this.isSubmitting.set(false);
        this.cerrarModalContratar();
      },
      error: (err) => {
        alert('Error al enviar solicitud: ' + (err.error?.mensaje || 'Intente nuevamente'));
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
  toggleFavorito(esp: EspecialistaDTO | PerfilEspecialista ) {
    alert('La funcionalidad de Favoritos estará disponible próximamente.');
  }
}

