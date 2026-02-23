import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { SolicitudEspecialistaService } from '../../../services/cliente/solicitud-especialista.service';
import { FichaCompletaSolicitud, FilterChip, MostrarSolicitud, SolicitudFilter } from '../../../models/cliente/solicitud-especialista.model';
import { ModalAlertaComponent } from "../../../components/cliente/modal-alerta.component/modal-alerta.component";
import { ModalDetalleSolicitud } from '../../../components/cliente/modal-detalle-solicitud.component/modal-detalle-solicitud.component';
import { FormsModule } from '@angular/forms';
import { DireccionOrden } from '../../../models/enums/enums.model';
import { ordenarDinamicamente } from '../../../utils/sort-utils';

@Component({
  selector: 'app-historial-solicitudes-especialista.pages',
  standalone: true,
  imports: [CommonModule, DatePipe, ModalAlertaComponent, ModalDetalleSolicitud, FormsModule],
  templateUrl: './historial-solicitudes-especialista.pages.html',
  styleUrl: './historial-solicitudes-especialista.pages.css',
})
export class HistorialSolicitudesEspecialistaPages implements OnInit {

  private solicitudService = inject(SolicitudEspecialistaService);

  misSolicitudes = signal<MostrarSolicitud[]>([]);
  mensajeError = signal<string | null>(null);
  mensajeExito = signal<string | null>(null);
  isLoading = signal(true);

  isAlertaOpen = signal(false);
  solicitudParaEliminar = signal<number | null>(null);

  isDetalleOpen = signal(false);
  solicitudDetalle = signal<FichaCompletaSolicitud | null>(null);

  // Variables para los inputs del HTML
  public searchText = '';
  public selectedState = '';
  public dateFrom = '';
  public dateTo = '';

  // VARIABLES PARA ORDENAMIENTO
  public criterioOrden = 'fechaSolicitud';
  public direccionOrden: DireccionOrden = 'desc';
  public dropdownOpen: string | null = null;

  // Señal para la lista de "Chips" (etiquetas) visibles
  public activeChips = signal<FilterChip[]>([]);

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes(filtros: SolicitudFilter = {}): void {
    this.isLoading.set(true);
    this.mensajeError.set(null);

    const tieneFiltros = Object.values(filtros).some(val => val !== undefined && val !== '');

    let requestObservable;

    if (tieneFiltros) {
      requestObservable = this.solicitudService.filtrarMisSolicitudes(filtros);
    } else {
      requestObservable = this.solicitudService.obtenerMisSolicitudes();
    }

    requestObservable.subscribe({
      next: (response) => {
        this.misSolicitudes.set(response.data);
        this.isLoading.set(false);
      },
      error: (err) => {
        if (err.status === 404 || err.status === 409 || err.error.message?.includes('No se encontraron')) {
          this.misSolicitudes.set([]);
          this.mensajeError.set(null);
        } else {
          this.mensajeError.set('Ocurrió un error al cargar las solicitudes.');
        }
        this.isLoading.set(false);
      }
    });
  }

  abrirModalEliminar(id: number): void {
    this.solicitudParaEliminar.set(id);
    this.isAlertaOpen.set(true);
  }

  onModalCancelar(): void {
    this.isAlertaOpen.set(false);
    this.solicitudParaEliminar.set(null);
  }
  onModalConfirmarEliminar(): void {
    const id = this.solicitudParaEliminar();
    if (id === null) return;

    console.log('Intentando eliminar solicitud con ID:', id);

    const listaOriginal = this.misSolicitudes();
    this.misSolicitudes.update((lista) => lista.filter(s => s.seId !== id));
    this.onModalCancelar();

    this.solicitudService.eliminarSolicitud(id).subscribe({
      next: () => {
        console.log('Eliminación exitosa en backend');
        this.mensajeExito.set('Solicitud eliminada correctamente.');
        setTimeout(() => this.mensajeExito.set(null), 3000);
      },
      error: (err) => {
        console.error('Error al eliminar en backend:', err);

        this.misSolicitudes.set(listaOriginal);

        this.mensajeError.set('Error: No se pudo eliminar la solicitud de la base de datos.');
      }
    });
  }


  abrirModalDetalle(id: number): void {
    this.solicitudDetalle.set(null);
    this.isDetalleOpen.set(true);

    this.solicitudService.obtenerDetalleSolicitud(id).subscribe({
      next: (response) => {
        this.solicitudDetalle.set(response.data);
      },
      error: (err) => {
        this.mensajeError.set(err.error.message || 'Error al cargar el detalle');
        this.isDetalleOpen.set(false);
      }
    });
  }

  cerrarModalDetalle(): void {
    this.isDetalleOpen.set(false);
    this.solicitudDetalle.set(null);
  }

  // --- FILTROS
  addFilter(type: 'motivo' | 'estado' | 'fechaDesde' | 'fechaHasta', value: string) {
    if (!value) return;

    let label = '';
    let key: keyof SolicitudFilter;

    switch (type) {
      case 'motivo':
        key = 'motivo';
        label = `🔍 "${value}"`;
        this.searchText = '';
        break;
      case 'estado':
        key = 'estado';
        label = `Estado: ${value}`;
        this.selectedState = '';
        break;
      case 'fechaDesde':
        key = 'fechaDesde';
        label = `Desde: ${value}`;
        break;
        case 'fechaHasta':
        key = 'fechaHasta';
        label = `📅 Hasta: ${value}`;
        break;
    }

    this.activeChips.update(chips => {
      const filtered = chips.filter(c => c.key !== key);
      return [...filtered, { key, label, value }];
    });

    this.aplicarCambios();
  }

  removeFilter(chip: FilterChip) {
    this.activeChips.update(chips => chips.filter(c => c !== chip));
    this.aplicarCambios();
  }

  clearAllFilters() {
    this.activeChips.set([]);
    this.aplicarCambios();
  }

 private aplicarCambios() {
    const filtrosDTO: SolicitudFilter = {};
    this.activeChips().forEach(chip => {
      (filtrosDTO as any)[chip.key] = chip.value;
    });

    this.isLoading.set(true);
    const request = Object.values(filtrosDTO).some(v => v)
      ? this.solicitudService.filtrarMisSolicitudes(filtrosDTO)
      : this.solicitudService.obtenerMisSolicitudes();

    request.subscribe({
      next: (response) => {
        this.direccionOrden = this.criterioOrden === 'fechaSolicitud' ? 'desc' : 'asc';

        const listaOrdenada = ordenarDinamicamente(
          response.data,
          this.criterioOrden,
          this.direccionOrden
        );

        this.misSolicitudes.set(listaOrdenada);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  toggleDropdown(menu: string, event: Event) {
    event.stopPropagation();
    this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
  }

  seleccionarOrden(criterio: string) {
    this.criterioOrden = criterio;
    this.dropdownOpen = null;
    this.aplicarCambios();
  }

}
