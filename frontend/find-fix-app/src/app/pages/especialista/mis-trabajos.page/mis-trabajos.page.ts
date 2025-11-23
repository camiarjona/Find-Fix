import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrabajoAppService } from '../../../services/trabajoApp-services/trabajo-app-service';
import { VisualizarTrabajoAppEspecialista } from '../../../models/trabajoApp-models/trabajo-app-model';

@Component({
  selector: 'app-mis-trabajos.page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-trabajos.page.html',
  styleUrl: './mis-trabajos.page.css',
})
export class MisTrabajosPage implements OnInit {
// Inyeccion de servicios
  private servicioTrabajoApp = inject(TrabajoAppService);
  // private servicioTrabajoExterno = inject(TrabajoExternoService);
  // private servicioEspecialista = inject(EspecialistaService); // Para obtener el ID del usuario actual
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

  estadosPosibles = ['CREADO', 'EN_PROCESO', 'FINALIZADO', 'CANCELADO'];

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
    //}this.cargarDatosReales();
    this.cargarDatosFalsos();
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
      case 'CREADO': permitidos.push('EN_PROCESO', 'CANCELADO'); break;
      case 'EN_PROCESO': permitidos.push('FINALIZADO', 'CANCELADO'); break;
      case 'FINALIZADO': case 'CANCELADO': break;
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


  //  LOGICA DE CARGA DE DATOS (FALSO VS REAL)


  cargarDatosFalsos() {
    this.estaCargando.set(true);
    setTimeout(() => {
      this.todosLosTrabajos = [
        {
          id: 101,
          titulo: 'Reparación de Fuga en Baño',
          estado: 'CREADO',
          origen: 'APP',
          descripcion: 'El cliente reporta una mancha de humedad.',
          presupuesto: 45000,
          fechaInicio: new Date('2023-11-23'),
          fechaFin: null,
          nombreCliente: 'Lionel Messi',
          emailEspecialista: 'leo@messi.com'
        } as any,
        {
          id: 102,
          titulo: 'Instalación Aire Acondicionado',
          estado: 'EN_PROCESO',
          origen: 'EXTERNO',
          descripcion: 'Instalación de equipo split.',
          presupuesto: 120000,
          fechaInicio: new Date('2023-11-20'),
          fechaFin: null,
          nombreCliente: 'Dibu Martínez',
        } as any,
        {
            id: 103,
            titulo: 'Pintura Completa Living',
            estado: 'FINALIZADO',
            origen: 'APP',
            descripcion: 'Pintura látex lavable blanco.',
            presupuesto: 85000,
            fechaInicio: new Date('2023-10-01'),
            fechaFin: new Date('2023-10-05'),
            nombreCliente: 'Angel Di Maria',
          } as any,
      ];
      this.aplicarFiltros();
      this.estaCargando.set(false);
    }, 800);
  }

  /*
  cargarDatosReales() {
    this.estaCargando.set(true);

     const idEspecialista = this.servicioEspecialista.obtenerIdUsuarioActual();

     this.servicioTrabajoApp.listarPorEspecialista(idEspecialista).subscribe({
    next: (trabajosApp) => {
     const listaApp = trabajosApp.map(t => ({...t, origen: 'APP'}));

    this.servicioTrabajoExterno.listarPorEspecialista(idEspecialista).subscribe({
    next: (trabajosExt) => {
    const listaExt = trabajosExt.map(t => ({...t, origen: 'EXTERNO'}));

    this.todosLosTrabajos = [...listaApp, ...listaExt];
    this.aplicarFiltros();
    this.estaCargando.set(false);
    }},
    error: (err) => { console.error('Error cargando externos', err); this.estaCargando.set(false); }
    });
    },
    error: (err) => {
    console.error('Error cargando trabajos app', err);
    this.mostrarAlerta('Error al conectar con el servidor', 'error');
    this.estaCargando.set(false);
    }
    });
  }
  */

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
      const d = new Date(this.filtros.desde); d.setHours(0,0,0,0);
      resultado = resultado.filter(t => new Date(t.fechaInicio) >= d);
    }
    if (this.filtros.hasta) {
      const h = new Date(this.filtros.hasta); h.setHours(23, 59, 59, 999);
      resultado = resultado.filter(t => new Date(t.fechaInicio) <= h);
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
    if(nuevoEstado === trabajo.estado) return;

    // Logica local (Visual inmediata)
    const estadoAnterior = trabajo.estado;
    trabajo.estado = nuevoEstado;
    this.actualizarFechasLogica(trabajo, nuevoEstado);

    /*
    CONEXIÓN BACKEND REAL
    const origen = (trabajo as any).origen;

    if (origen === 'APP') {
        this.servicioTrabajoApp.cambiarEstado(trabajo.id, nuevoEstado).subscribe({
        next: () => this.mostrarAlerta('Estado actualizado en servidor', 'success'),
        error: () => {
        trabajo.estado = estadoAnterior; // Revertir si falla
        this.mostrarAlerta('Error al actualizar estado', 'error');
        }
        });
    } else {
       this.servicioTrabajoExterno.cambiarEstado(trabajo.id, nuevoEstado)...
    }
    */

    this.mostrarAlerta(`Estado actualizado a: ${this.formatearTextoEstado(nuevoEstado)}`, 'success');
  }

  actualizarFechasLogica(trabajo: any, estado: string) {
    if (estado === 'EN_PROCESO' && !trabajo.fechaInicio) trabajo.fechaInicio = new Date();
    if (estado === 'FINALIZADO') trabajo.fechaFin = new Date();
    if (estado !== 'FINALIZADO' && estado !== 'CANCELADO') trabajo.fechaFin = null;
  }

  // --- Modal Detalle ---
  abrirModalDetalle(trabajo: VisualizarTrabajoAppEspecialista) {
    this.datosEdicion = {
      titulo: trabajo.titulo,
      estado: trabajo.estado,
      fechaFin: trabajo.fechaFin ? new Date(trabajo.fechaFin).toISOString().split('T')[0] : '',
      presupuesto: trabajo.presupuesto,
      descripcion: trabajo.descripcion
    };
    this.modoEdicion = { titulo: false, estado: false, fechaFin: false, presupuesto: false, descripcion: false };
    this.trabajoSeleccionado.set(trabajo);
  }

  cerrarModal() {
    this.trabajoSeleccionado.set(null);
  }

  activarEdicionCampo(campo: keyof typeof this.modoEdicion) {
    this.modoEdicion[campo] = true;
  }

  guardarEdicionModal() {
    const trabajo = this.trabajoSeleccionado();
    if(trabajo) {
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

      /*
      CONEXIÓN BACKEND REAL ---
      const dtoActualizar = {
         titulo: this.datosEdicion.titulo,
         presupuesto: this.datosEdicion.presupuesto,
         descripcion: this.datosEdicion.descripcion,
         estado: this.datosEdicion.estado,
         fechaFin: this.datosEdicion.fechaFin
      };

      if ((trabajo as any).origen === 'APP') {
        this.servicioTrabajoApp.actualizar(trabajo.id, dtoActualizar).subscribe(...)
      } else {
        this.servicioTrabajoExterno.actualizar(trabajo.id, dtoActualizar).subscribe(...)
      }
      */

      this.cerrarModal();
      this.mostrarAlerta('Cambios guardados correctamente.', 'success');
    }
  }

  //
  //  NUEVO TRABAJO EXTERNO (MODAL CREACIÓN)
  //

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

    // Simulación local
    const trabajoSimulado: any = {
      id: Math.floor(Math.random() * 1000), // ID temporal
      ...this.nuevoTrabajo,
      estado: 'CREADO',
      origen: 'EXTERNO',
      fechaFin: null
    };

    // Lo agregamos a la lista visual
    this.todosLosTrabajos.unshift(trabajoSimulado);
    this.aplicarFiltros();

    /*
    CONEXIÓN BACKEND REAL
    const dtoCrear = { ...this.nuevoTrabajo, especialistaId: ... };
    this.servicioTrabajoExterno.crear(dtoCrear).subscribe({
    next: (trabajoCreado) => {
    this.mostrarAlerta('Trabajo creado con éxito', 'success');
    this.cargarDatosReales(); // Recargar todo para asegurar sincronización
    this.cerrarModalCreacion();
    },
    error: (err) => this.mostrarAlerta('Error al crear trabajo', 'error')
    });
    */

    this.mostrarAlerta('Trabajo externo creado exitosamente.', 'success');
    this.cerrarModalCreacion();
  }
}
