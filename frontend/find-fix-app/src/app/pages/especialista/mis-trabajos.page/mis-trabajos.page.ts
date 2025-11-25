import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrabajoAppService } from '../../../services/trabajoApp-services/trabajo-app-service';
import { VisualizarTrabajoAppEspecialista } from '../../../models/trabajoApp-models/trabajo-app-model';
import { TrabajoExternoService } from '../../../services/trabajoExterno-services/trabajo-externo-service';

@Component({
  selector: 'app-mis-trabajos.page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-trabajos.page.html',
  styleUrl: './mis-trabajos.page.css',
})
export class MisTrabajosPage implements OnInit {
  // --- Inyeccion de servicios
  private servicioTrabajoApp = inject(TrabajoAppService);
  private servicioTrabajoExterno = inject(TrabajoExternoService);
  private ruta = inject(ActivatedRoute);

  // --- Datos Principales ---
  trabajosVisibles = signal<VisualizarTrabajoAppEspecialista[]>([]);
  todosLosTrabajos: VisualizarTrabajoAppEspecialista[] = [];
  estaCargando = signal(true);
  modoVista: 'tarjetas' | 'lista' = 'tarjetas';

  // --- Alertas ---
  alertaVisible = signal(false);
  mensajeAlerta = signal('');
  tipoAlerta = signal<'success' | 'error'>('success');

  // --- Filtros ---
  filtros = {
    origen: '',
    id: '',
    titulo: '',
    estado: '',
    desde: '',
    hasta: ''
  };

  estadosPosibles = ['CREADO', 'EN_PROCESO', 'FINALIZADO'];

  // --- Modal Detalle/Edición ---
  trabajoSeleccionado = signal<VisualizarTrabajoAppEspecialista | null>(null);
  datosEdicion: any = {};
  modoEdicion = {
    titulo: false,
    estado: false,
    fechaFin: false,
    presupuesto: false,
    descripcion: false
  };

  // --- Modal Creación ---
  modalCreacionVisible = signal(false);
  nuevoTrabajo = {
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

  mostrarAlerta(mensaje: string, tipo: 'success' | 'error' = 'success') {
    this.mensajeAlerta.set(mensaje);
    this.tipoAlerta.set(tipo);
    this.alertaVisible.set(true);
  }

  cerrarAlerta() {
    this.alertaVisible.set(false);
  }

  // --- MÉTODOS DE CONVERSIÓN ESTADO (NUEVOS) ---
  private normalizarEstadoBackend(estado: string): string {
    if (!estado) return '';
    const mapaEstados: { [key: string]: string } = {
      'Creado': 'CREADO',
      'En proceso': 'EN_PROCESO',
      'En revision': 'EN_REVISION',
      'Finalizado': 'FINALIZADO',
      'CREADO': 'CREADO',
      'EN_PROCESO': 'EN_PROCESO',
      'EN_REVISION': 'EN_REVISION',
      'FINALIZADO': 'FINALIZADO',
    };
    return mapaEstados[estado] || estado.toUpperCase().replace(/\s+/g, '_');
  }

  private obtenerEstadoParaBackend(estadoFrontend: string): string {
    const mapaBackend: { [key: string]: string } = {
      'CREADO': 'Creado',
      'EN_PROCESO': 'En proceso',
      'EN_REVISION': 'En revision',
      'FINALIZADO': 'Finalizado',
    };
    return mapaBackend[estadoFrontend] || estadoFrontend;
  }

  cargarDatosReales() {
    this.estaCargando.set(true);

    // Primero obtenemos los trabajos desde la app para el especialista
    this.servicioTrabajoApp.obtenerTrabajosEspecialista().subscribe({
      next: (respApp) => {
        const listaApp = (respApp.data || []).map(t => {
          const item = { ...(t as any), origen: 'APP' } as any;
          this.normalizeTrabajoDates(item);
          return item;
        });

        // Luego obtenemos trabajos externos
        this.servicioTrabajoExterno.obtenerMisTrabajos().subscribe({
          next: (respExt) => {
            const listaExt = (respExt.data || []).map((t: any) => {
              const item = { ...(t as any), origen: 'EXTERNO' } as any;
              this.normalizeTrabajoDates(item);
              return item;
            });

            // Unificamos listas
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

  // Filtros
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
    this.trabajosVisibles.set(resultado);
  }

  limpiarFiltros() {
    this.filtros = { origen: '', id: '', titulo: '', estado: '', desde: '', hasta: '' };
    this.aplicarFiltros();
  }

  // --- ACTUALIZACIÓN DE ESTADOS Y EDICIÓN ---

  cambiarEstadoRapido(trabajo: VisualizarTrabajoAppEspecialista, event: Event) {
    event.stopPropagation();
    const nuevoEstado = (event.target as HTMLSelectElement).value;
    if (nuevoEstado === trabajo.estado) return;

    // Logica local
    const estadoAnterior = trabajo.estado;
    trabajo.estado = nuevoEstado;
    this.actualizarFechasLogica(trabajo, nuevoEstado);

    const estadoParaBackend = this.obtenerEstadoParaBackend(nuevoEstado);
    const origen = (trabajo as any).origen || 'APP';
    const titulo = (trabajo as any).titulo;

    if (origen === 'APP') {
      this.servicioTrabajoApp.actualizarEstadoTrabajo(titulo, estadoParaBackend).subscribe({
        next: () => this.mostrarAlerta('Estado actualizado en servidor', 'success'),
        error: (err) => {
          console.error('Error actualizando estado APP', err);
          trabajo.estado = estadoAnterior;
          const serverMsg = err?.error?.message || err?.error?.mensaje || err?.message || `Error ${err?.status || ''}`;
          this.mostrarAlerta(serverMsg, 'error');
        }
      });
    } else {
      this.servicioTrabajoExterno.actualizarEstado(titulo, estadoParaBackend).subscribe({
        next: () => this.mostrarAlerta('Estado actualizado en servidor', 'success'),
        error: (err) => {
          console.error('Error actualizando estado EXTERNO', err);
          trabajo.estado = estadoAnterior;
          const serverMsg = err?.error?.message || err?.error?.mensaje || err?.message || `Error ${err?.status || ''}`;
          this.mostrarAlerta(serverMsg, 'error');
        }
      });
    }
  }

  actualizarFechasLogica(trabajo: any, estado: string) {
    if (estado === 'EN_PROCESO' && !trabajo.fechaInicio) trabajo.fechaInicio = new Date();
    if (estado === 'FINALIZADO') trabajo.fechaFin = new Date();
    if (estado !== 'FINALIZADO') trabajo.fechaFin = null;
  }

  // Normaliza valores de fecha que puedan venir en diferentes formatos
  private parseBackendDate(value: any): string | null {
    if (!value) return null;
    if (typeof value === 'string') {
      if (/^\d{4}-\d{2}-\d{2}/.test(value)) return value;
      const m = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(value);
      if (m) {
        const d = Number(m[1]);
        const mo = Number(m[2]) - 1;
        const y = Number(m[3]);
        return new Date(Date.UTC(y, mo, d)).toISOString();
      }
      return null;
    }
    if (value instanceof Date) return value.toISOString();
    if (typeof value === 'number') return new Date(value).toISOString();
    return null;
  }

  private normalizeTrabajoDates(t: any) {
    t.fechaInicio = this.parseBackendDate(t.fechaInicio) || null;
    t.fechaFin = this.parseBackendDate(t.fechaFin) || null;
    // AQUI USAMOS LA NORMALIZACIÓN PARA ASEGURARNOS DE TENER "EN_PROCESO"
    t.estado = this.normalizarEstadoBackend(t.estado || '');
  }

  // --- Modal Detalle ---
  abrirModalDetalle(trabajo: VisualizarTrabajoAppEspecialista) {
    const origen = (trabajo as any).origen || 'APP';
    if (origen === 'APP') {
      // Obtener ficha detallada desde el backend para trabajos APP
      const tituloBuscado = trabajo.titulo;
      this.estaCargando.set(true);
      this.servicioTrabajoApp.obtenerFichaEspecialista(tituloBuscado).subscribe({
        next: (resp) => {
          const detalle = { ...(resp.data as any), origen: 'APP' } as any;
          // Normalize dates and ensure estado is present and normalized
          this.normalizeTrabajoDates(detalle);
          // Si por alguna razón la ficha no trae estado, usamos el de la lista
          detalle.estado = detalle.estado || (trabajo as any).estado || '';

          console.debug('Ficha recibida (APP):', detalle);
          this.datosEdicion = {
            titulo: detalle.titulo,
            estado: detalle.estado,
            fechaFin: detalle.fechaFin ? new Date(detalle.fechaFin).toISOString().split('T')[0] : '',
            presupuesto: detalle.presupuesto,
            descripcion: detalle.descripcion
          };
          this.modoEdicion = { titulo: false, estado: false, fechaFin: false, presupuesto: false, descripcion: false };
          this.trabajoSeleccionado.set(detalle);
          this.estaCargando.set(false);
        },
        error: (err) => {
          console.error('Error obteniendo ficha del trabajo APP', err);
          // Fallback: mostrar el trabajo que ya tenemos
          this.prepararDatosEdicionFallback(trabajo);
          this.trabajoSeleccionado.set(trabajo);
          this.estaCargando.set(false);
          this.mostrarAlerta('No se pudo obtener detalles completos del trabajo', 'error');
        }
      });
    } else {
      // Trabajo EXTERNO: usar los datos que ya tenemos
      this.prepararDatosEdicionFallback(trabajo);
      this.trabajoSeleccionado.set(trabajo);
    }
  }

  private prepararDatosEdicionFallback(trabajo: VisualizarTrabajoAppEspecialista) {
    this.datosEdicion = {
      titulo: trabajo.titulo,
      estado: trabajo.estado,
      fechaFin: trabajo.fechaFin ? new Date(trabajo.fechaFin).toISOString().split('T')[0] : '',
      presupuesto: trabajo.presupuesto,
      descripcion: trabajo.descripcion
    };
    this.modoEdicion = { titulo: false, estado: false, fechaFin: false, presupuesto: false, descripcion: false };
  }

  cerrarModal() {
    this.trabajoSeleccionado.set(null);
  }

  activarEdicionCampo(campo: keyof typeof this.modoEdicion) {
    this.modoEdicion[campo] = true;
  }

  guardarEdicionModal() {
    const trabajo = this.trabajoSeleccionado();
    if (trabajo) {
      const estadoOriginal = (trabajo as any).estado;

      trabajo.titulo = this.datosEdicion.titulo;
      trabajo.estado = this.datosEdicion.estado;
      trabajo.presupuesto = this.datosEdicion.presupuesto;
      trabajo.descripcion = this.datosEdicion.descripcion;
      if (this.datosEdicion.fechaFin) {
        trabajo.fechaFin = (this.datosEdicion.fechaFin);
      } else {
        trabajo.fechaFin = null as any;
      }
      this.actualizarFechasLogica(trabajo, trabajo.estado);

      const estadoBackend = this.obtenerEstadoParaBackend(this.datosEdicion.estado);

      const dtoActualizarApp: any = {
        titulo: this.datosEdicion.titulo,
        descripcion: this.datosEdicion.descripcion,
        presupuesto: this.datosEdicion.presupuesto,
      };

      const dtoActualizarExt: any = {
        titulo: this.datosEdicion.titulo,
        descripcion: this.datosEdicion.descripcion,
        presupuesto: this.datosEdicion.presupuesto,
      };

      const origen = (trabajo as any).origen || 'APP';
      const tituloOriginal = (trabajo as any).titulo;

      if (origen === 'APP') {
        this.servicioTrabajoApp.actualizarDatosTrabajo(tituloOriginal, dtoActualizarApp).subscribe({
          next: () => {
            if (this.datosEdicion.estado !== (trabajo as any).estadoOriginal) {
              this.servicioTrabajoApp.actualizarEstadoTrabajo(this.datosEdicion.titulo, estadoBackend).subscribe();
            }
            this.mostrarAlerta('Cambios guardados en servidor', 'success');
            this.cargarDatosReales();
          },
          error: (err) => {
            console.error('Error guardando cambios APP', err);
            this.mostrarAlerta('Error al guardar cambios', 'error');
          }
        });
      } else {
        this.servicioTrabajoExterno.modificarTrabajo(tituloOriginal, dtoActualizarExt).subscribe({
          next: () => {
            if (this.datosEdicion.estado !== estadoOriginal) {
              this.servicioTrabajoExterno.actualizarEstado(tituloOriginal, estadoBackend).subscribe({
                next: () => {
                  this.mostrarAlerta('Cambios y estado guardados', 'success');
                  this.cargarDatosReales();
                },
                error: (e) => console.error("Error actualizando estado externo", e)
              });
            } else {
              this.mostrarAlerta('Cambios guardados en servidor', 'success');
              this.cargarDatosReales();
            }
          },
          error: (err) => {
            console.error('Error guardando cambios EXTERNO', err);
            const msg = err.error?.message || 'Error al guardar cambios. Revisa los campos.';
            this.mostrarAlerta(msg, 'error');
          }
        });
      }

      this.cerrarModal();
      this.mostrarAlerta('Cambios guardados correctamente.', 'success');
    }
  }

  //  NUEVO TRABAJO EXTERNO (MODAL CREACIÓN)
  agregarTrabajoExterno() {
    this.nuevoTrabajo = {
      titulo: '',
      nombreCliente: '',
      descripcion: '',
      presupuesto: null,
      fechaInicio: new Date().toISOString().split('T')[0]
    };
    this.modalCreacionVisible.set(true);
  }

  cerrarModalCreacion() {
    this.modalCreacionVisible.set(false);
  }

  guardarNuevoTrabajo() {
    if (!this.nuevoTrabajo.titulo || !this.nuevoTrabajo.nombreCliente) {
      this.mostrarAlerta('Por favor completa el título y el cliente.', 'error');
      return;
    }

    // CONEXIÓN BACKEND REAL
    const dtoCrear = {
      titulo: this.nuevoTrabajo.titulo,
      nombreCliente: this.nuevoTrabajo.nombreCliente,
      descripcion: this.nuevoTrabajo.descripcion,
      presupuesto: this.nuevoTrabajo.presupuesto ?? 0
    };
    this.servicioTrabajoExterno.crearTrabajo(dtoCrear).subscribe({
      next: (resp) => {
        // Si el backend devuelve el trabajo, lo agregamos o recargamos
        const creado = (resp && (resp as any).data) || null;
        if (creado) {
          const item = { ...creado, origen: 'EXTERNO' } as any;
          this.normalizeTrabajoDates(item); // Normalizar el nuevo item también
          // colocar al principio
          this.todosLosTrabajos.unshift(item);
          this.aplicarFiltros();
        } else {
          // si no devuelve, recargar la lista completa
          this.cargarDatosReales();
        }
        this.mostrarAlerta('Trabajo creado con éxito', 'success');
        this.cerrarModalCreacion();
      },
      error: (err) => {
        console.error('Error creando trabajo externo', err);
        this.mostrarAlerta('Error al crear trabajo', 'error');
      }
    });
  }
}
