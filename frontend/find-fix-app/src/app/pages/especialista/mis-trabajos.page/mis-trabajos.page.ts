import { Component, inject, OnInit, signal, WritableSignal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrabajoAppService } from '../../../services/trabajoApp-services/trabajo-app-service';
import { VisualizarTrabajoAppEspecialista } from '../../../models/trabajoApp-models/trabajo-app-model';
import { TrabajoExternoService } from '../../../services/trabajoExterno-services/trabajo-externo-service';
import { DireccionOrden } from '../../../models/enums/enums.model';
import { ordenarDinamicamente } from '../../../utils/sort-utils';

@Component({
  selector: 'app-mis-trabajos.page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-trabajos.page.html',
  styleUrl: './mis-trabajos.page.css',
})
export class MisTrabajosPage implements OnInit {

  // --- Inyeccion de servicios ---
  private servicioTrabajoApp = inject(TrabajoAppService);
  private servicioTrabajoExterno = inject(TrabajoExternoService);
  private ruta = inject(ActivatedRoute);

  // VARIABLES PARA ORDENAMIENTO
  public criterioOrden = 'fechaInicio';
  public direccionOrden: DireccionOrden = 'desc';
  public dropdownOpen: string | null = null;

  // --- Datos Principales ---
  public todosLosTrabajos: VisualizarTrabajoAppEspecialista[] = []; // Datos unificados brutos
  public trabajosFiltrados: VisualizarTrabajoAppEspecialista[] = []; // Resultado de aplicar filtros
  public trabajosVisibles = signal<VisualizarTrabajoAppEspecialista[]>([]); // Lo que se ve en la página actual
  public estaCargando = signal(true);
  public modoVista: 'tarjetas' | 'lista' = 'tarjetas';

  public currentPage = signal(0);
  public pageSize = 6;
  public totalPages = signal(0);

  public alertaVisible = signal(false);
  public mensajeAlerta = signal('');
  public tipoAlerta = signal<'success' | 'error'>('success');

  public filtros = {
    origen: '',
    id: '',
    titulo: '',
    estado: '',
    desde: '',
    hasta: ''
  };

  public estadosPosibles = ['CREADO', 'EN_PROCESO', 'FINALIZADO'];

  public trabajoSeleccionado = signal<VisualizarTrabajoAppEspecialista | null>(null);
  public datosEdicion: any = {};
  public modoEdicion = {
    titulo: false,
    estado: false,
    fechaFin: false,
    presupuesto: false,
    descripcion: false
  };

  public modalCreacionVisible = signal(false);
  public nuevoTrabajo = {
    titulo: '',
    nombreCliente: '',
    descripcion: '',
    presupuesto: null as number | null,
    fechaInicio: new Date().toISOString().split('T')[0]
  };

  ngOnInit() {
    this.cargarDatosReales();
  }

  establecerModoVista(modo: 'tarjetas' | 'lista') {
    this.modoVista = modo;
  }

  // --- Lógica de Carga y Datos ---
  cargarDatosReales() {
    this.estaCargando.set(true);

    this.servicioTrabajoApp.obtenerTrabajosEspecialista().subscribe({
      next: (respApp) => {
        const listaApp = (respApp.data || []).map(t => {
          const item = { ...(t as any), origen: 'APP' } as any;
          this.normalizeTrabajoDates(item);
          return item;
        });

        this.servicioTrabajoExterno.obtenerMisTrabajos().subscribe({
          next: (respExt) => {
            const listaExt = (respExt.data || []).map((t: any) => {
              const item = { ...(t as any), origen: 'EXTERNO' } as any;
              this.normalizeTrabajoDates(item);
              return item;
            });

            this.todosLosTrabajos = [...listaApp, ...listaExt];
            this.aplicarFiltros();
            this.estaCargando.set(false);
          },
          error: (err) => {
            console.error('Error cargando trabajos externos', err);
            this.todosLosTrabajos = [...listaApp];
            this.aplicarFiltros();
            this.estaCargando.set(false);
            this.mostrarAlerta('Error al cargar trabajos externos', 'error');
          }
        });
      },
      error: (err) => {
        console.error('Error cargando trabajos app', err);
        this.estaCargando.set(false);
        this.mostrarAlerta('Error al conectar con el servidor', 'error');
      }
    });
  }

  aplicarFiltros() {
    let resultado = this.todosLosTrabajos;

    if (this.filtros.origen) {
      resultado = resultado.filter(t => (t as any).origen === this.filtros.origen);
    }
    if (this.filtros.titulo) {
      resultado = resultado.filter(t => t.titulo.toLowerCase().includes(this.filtros.titulo.toLowerCase()));
    }
    if (this.filtros.estado) {
      resultado = resultado.filter(t => t.estado === this.filtros.estado);
    }
    if (this.filtros.desde) {
      const d = new Date(this.filtros.desde); d.setHours(0, 0, 0, 0);
      resultado = resultado.filter(t => t.fechaInicio && new Date(t.fechaInicio) >= d);
    }
    if (this.filtros.hasta) {
      const h = new Date(this.filtros.hasta); h.setHours(23, 59, 59, 999);
      resultado = resultado.filter(t => t.fechaInicio && new Date(t.fechaInicio) <= h);
    }

    this.direccionOrden = 'desc';

    this.trabajosFiltrados = ordenarDinamicamente(
      resultado,
      this.criterioOrden,
      this.direccionOrden
    );

    this.trabajosFiltrados = resultado;
    this.totalPages.set(Math.ceil(this.trabajosFiltrados.length / this.pageSize));

    this.currentPage.set(0);
    this.actualizarVistaPaginada();
  }

  toggleDropdown(menu: string, event: Event) {
    event.stopPropagation();
    this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
  }

  seleccionarOrden(criterio: string) {
    this.criterioOrden = criterio;
    this.dropdownOpen = null;
    this.aplicarFiltros();
  }

  actualizarVistaPaginada() {
    const inicio = this.currentPage() * this.pageSize;
    const fin = inicio + this.pageSize;
    this.trabajosVisibles.set(this.trabajosFiltrados.slice(inicio, fin));
  }

  cambiarPagina(nuevaPagina: number) {
    this.currentPage.set(nuevaPagina);
    this.actualizarVistaPaginada();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  limpiarFiltros() {
    this.filtros = { origen: '', id: '', titulo: '', estado: '', desde: '', hasta: '' };
    this.aplicarFiltros();
  }

  formatearTextoEstado(estado: string): string {
    if (!estado) return '';
    const texto = estado.replace(/_/g, ' ').toLowerCase();
    return texto.charAt(0).toUpperCase() + texto.slice(1);
  }

  obtenerClaseEstado(estado: string): string {
    return estado ? estado.toLowerCase() : 'default';
  }

  obtenerEstadosPermitidos(estadoActual: string): string[] {
    const permitidos = [estadoActual];
    switch (estadoActual) {
      case 'CREADO': permitidos.push('EN_PROCESO'); break;
      case 'EN_PROCESO': permitidos.push('FINALIZADO'); break;
      case 'FINALIZADO': break;
      default: return this.estadosPosibles;
    }
    return [...new Set(permitidos)];
  }

  cambiarEstadoRapido(trabajo: VisualizarTrabajoAppEspecialista, event: Event) {
    event.stopPropagation();
    const nuevoEstado = (event.target as HTMLSelectElement).value;
    if (nuevoEstado === trabajo.estado) return;

    const estadoAnterior = trabajo.estado;
    trabajo.estado = nuevoEstado;
    this.actualizarFechasLogica(trabajo, nuevoEstado);

    const estadoParaBackend = this.obtenerEstadoParaBackend(nuevoEstado);
    const origen = (trabajo as any).origen || 'APP';
    const titulo = (trabajo as any).titulo;

    const serv = origen === 'APP' ? this.servicioTrabajoApp : this.servicioTrabajoExterno;

    const observable = origen === 'APP'
      ? this.servicioTrabajoApp.actualizarEstadoTrabajo(titulo, estadoParaBackend)
      : this.servicioTrabajoExterno.actualizarEstado(titulo, estadoParaBackend);

    observable.subscribe({
      next: () => this.mostrarAlerta('Estado actualizado', 'success'),
      error: (err) => {
        trabajo.estado = estadoAnterior;
        const msg = err?.error?.message || 'Error al actualizar estado';
        this.mostrarAlerta(msg, 'error');
      }
    });
  }

  actualizarFechasLogica(trabajo: any, estado: string) {
    if (estado === 'EN_PROCESO' && !trabajo.fechaInicio) trabajo.fechaInicio = new Date();
    if (estado === 'FINALIZADO') trabajo.fechaFin = new Date();
    if (estado !== 'FINALIZADO') trabajo.fechaFin = null;
  }

  private normalizarEstadoBackend(estado: string): string {
    if (!estado) return '';
    const mapaEstados: { [key: string]: string } = {
      'Creado': 'CREADO', 'En proceso': 'EN_PROCESO', 'En revision': 'EN_REVISION', 'Finalizado': 'FINALIZADO',
      'CREADO': 'CREADO', 'EN_PROCESO': 'EN_PROCESO', 'EN_REVISION': 'EN_REVISION', 'FINALIZADO': 'FINALIZADO',
    };
    return mapaEstados[estado] || estado.toUpperCase().replace(/\s+/g, '_');
  }

  private obtenerEstadoParaBackend(estadoFrontend: string): string {
    const mapaBackend: { [key: string]: string } = {
      'CREADO': 'Creado', 'EN_PROCESO': 'En proceso', 'EN_REVISION': 'En revision', 'FINALIZADO': 'Finalizado',
    };
    return mapaBackend[estadoFrontend] || estadoFrontend;
  }

  private normalizeTrabajoDates(t: any) {
    t.fechaInicio = this.parseBackendDate(t.fechaInicio) || null;
    t.fechaFin = this.parseBackendDate(t.fechaFin) || null;
    t.estado = this.normalizarEstadoBackend(t.estado || '');
  }

  private parseBackendDate(value: any): string | null {
    if (!value) return null;
    if (typeof value === 'string') {
      if (/^\d{4}-\d{2}-\d{2}/.test(value)) return value;
      const m = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(value);
      if (m) return new Date(Date.UTC(Number(m[3]), Number(m[2]) - 1, Number(m[1]))).toISOString();
      return null;
    }
    return (value instanceof Date || typeof value === 'number') ? new Date(value).toISOString() : null;
  }

  abrirModalDetalle(trabajo: VisualizarTrabajoAppEspecialista) {
  const origen = (trabajo as any).origen || 'APP';

  if (origen === 'APP') {
    this.estaCargando.set(true);

    this.servicioTrabajoApp.obtenerFichaEspecialista(trabajo.titulo).subscribe({
      next: (resp) => {
        const detalle = { ...(resp.data as any), origen: 'APP' };
        this.normalizeTrabajoDates(detalle);
        this.prepararDatosEdicionFallback(detalle);
        this.trabajoSeleccionado.set(detalle);
        this.estaCargando.set(false);
      },
      error: (err) => {
        console.error('Error al obtener ficha:', err);
        this.prepararDatosEdicionFallback(trabajo);
        this.trabajoSeleccionado.set(trabajo);
        this.estaCargando.set(false);
      }
    });
  } else {
    this.prepararDatosEdicionFallback(trabajo);
    this.trabajoSeleccionado.set(trabajo);
    this.estaCargando.set(false);
  }
}

  private prepararDatosEdicionFallback(trabajo: any) {
    this.datosEdicion = {
      titulo: trabajo.titulo,
      estado: trabajo.estado,
      fechaFin: trabajo.fechaFin ? new Date(trabajo.fechaFin).toISOString().split('T')[0] : '',
      presupuesto: trabajo.presupuesto,
      descripcion: trabajo.descripcion
    };
    this.modoEdicion = { titulo: false, estado: false, fechaFin: false, presupuesto: false, descripcion: false };
  }

  cerrarModal() { this.trabajoSeleccionado.set(null); }
  activarEdicionCampo(campo: keyof typeof this.modoEdicion) { this.modoEdicion[campo] = true; }

  guardarEdicionModal() {
    const trabajo = this.trabajoSeleccionado();
    if (!trabajo) return;

    const origen = (trabajo as any).origen || 'APP';
    const tituloOriginal = (trabajo as any).titulo;
    const dto = {
      titulo: this.datosEdicion.titulo,
      descripcion: this.datosEdicion.descripcion,
      presupuesto: this.datosEdicion.presupuesto
    };

    const observable = origen === 'APP'
      ? this.servicioTrabajoApp.actualizarDatosTrabajo(tituloOriginal, dto)
      : this.servicioTrabajoExterno.modificarTrabajo(tituloOriginal, dto);

    observable.subscribe({
      next: () => {
        const estadoBackend = this.obtenerEstadoParaBackend(this.datosEdicion.estado);
        const obsEstado = origen === 'APP'
          ? this.servicioTrabajoApp.actualizarEstadoTrabajo(this.datosEdicion.titulo, estadoBackend)
          : this.servicioTrabajoExterno.actualizarEstado(tituloOriginal, estadoBackend);

        obsEstado.subscribe(() => {
          this.mostrarAlerta('Cambios guardados', 'success');
          this.cargarDatosReales();
        });
      },
      error: () => this.mostrarAlerta('Error al guardar', 'error')
    });
    this.cerrarModal();
  }

  agregarTrabajoExterno() {
    this.nuevoTrabajo = { titulo: '', nombreCliente: '', descripcion: '', presupuesto: null, fechaInicio: new Date().toISOString().split('T')[0] };
    this.modalCreacionVisible.set(true);
  }

  guardarNuevoTrabajo() {
    if (!this.nuevoTrabajo.titulo || !this.nuevoTrabajo.nombreCliente) return;
    this.servicioTrabajoExterno.crearTrabajo({
      titulo: this.nuevoTrabajo.titulo,
      nombreCliente: this.nuevoTrabajo.nombreCliente,
      descripcion: this.nuevoTrabajo.descripcion,
      presupuesto: this.nuevoTrabajo.presupuesto ?? 0
    }).subscribe({
      next: () => {
        this.mostrarAlerta('Trabajo creado', 'success');
        this.cargarDatosReales();
        this.modalCreacionVisible.set(false);
      },
      error: () => this.mostrarAlerta('Error al crear', 'error')
    });
  }

  mostrarAlerta(mensaje: string, tipo: 'success' | 'error' = 'success') {
    this.mensajeAlerta.set(mensaje);
    this.tipoAlerta.set(tipo);
    this.alertaVisible.set(true);
  }
  cerrarAlerta() { this.alertaVisible.set(false); }
}
