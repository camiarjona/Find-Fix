import { Component, inject, signal } from '@angular/core';
import { EspecialistaService } from '../../../services/especialista/especialista.service';
import { ActivatedRoute } from '@angular/router';
import { TrabajoEspecialista } from '../../../models/especialista/especialista.model';

@Component({
  selector: 'app-mis-trabajos.page',
  imports: [],
  templateUrl: './mis-trabajos.page.html',
  styleUrl: './mis-trabajos.page.css',
})
export class MisTrabajosPage {
private especialistaService = inject(EspecialistaService);
  private route = inject(ActivatedRoute);

  // private todosLosTrabajos: TrabajoEspecialista[] = [];

  // Señales para la vista
  trabajosVisibles = signal<TrabajoEspecialista[]>([]);
  tituloPagina = signal('Todos mis Trabajos');
  isLoading = signal(true);

  // Almacena TODOS los trabajos (Base de datos local falsa)
  private todosLosTrabajos: TrabajoEspecialista[] = [];
ngOnInit() {
    // Comentamos la carga real para usar mocks
    // this.cargarTrabajosReales();
    this.cargarDatosFalsos();
  }

  cargarDatosFalsos() {
    this.isLoading.set(true);

    // Simulamos un pequeño retraso de red para ver el spinner
    setTimeout(() => {
      this.todosLosTrabajos = [
        {
          id: 1,
          titulo: 'Instalación Aire Acondicionado Split',
          estado: 'EN_PROCESO',
          fechaInicio: new Date(), // Fecha de hoy
          nombreCliente: 'Lionel Messi',
          descripcion: 'Instalación completa en 3er piso con ménsula.',
          presupuesto: 150000,
          tipo: 'APP' // Icono de celular
        },
        {
          id: 2,
          titulo: 'Reparación de Fuga de Gas',
          estado: 'EN_PROCESO',
          fechaInicio: '2023-11-15',
          nombreCliente: 'Antonela Roccuzzo',
          descripcion: 'Fuga detectada en la cocina, requiere cambio de llave de paso.',
          presupuesto: 45000,
          tipo: 'EXTERNO' // Icono de herramienta (trabajo particular)
        },
        {
          id: 3,
          titulo: 'Pintura Habitación Principal',
          estado: 'FINALIZADO',
          fechaInicio: '2023-10-01',
          fechaFin: '2023-10-03',
          nombreCliente: 'Sergio Agüero',
          descripcion: 'Dos manos de látex lavable blanco.',
          presupuesto: 80000,
          tipo: 'APP'
        },
        {
          id: 4,
          titulo: 'Cambio de Cableado Eléctrico',
          estado: 'FINALIZADO',
          fechaInicio: '2023-09-20',
          fechaFin: '2023-09-25',
          nombreCliente: 'Rodrigo De Paul',
          descripcion: 'Recableado completo del quincho.',
          presupuesto: 200000,
          tipo: 'EXTERNO'
        },
        {
          id: 5,
          titulo: 'Arreglo de Persiana',
          estado: 'ACEPTADO', // Aceptado pero no iniciado
          fechaInicio: 'Por definir',
          nombreCliente: 'Emiliano Martínez',
          descripcion: 'Se trabó la cinta, hay que cambiar el enrollador.',
          presupuesto: 25000,
          tipo: 'APP'
        }
      ];

      // Una vez cargados los datos falsos, activamos el filtro de la URL
      this.escucharFiltros();
      this.isLoading.set(false);
    }, 800);
  }

  escucharFiltros() {
    this.route.queryParams.subscribe(params => {
      const filtro = params['filtro'] || 'TODOS';
      this.aplicarFiltro(filtro);
    });
  }

  cargarTrabajos() {
    this.isLoading.set(true);

    this.especialistaService.getMisTrabajos().subscribe({
      next: (data) => {
        this.todosLosTrabajos = data;

        this.route.queryParams.subscribe(params => {
          const filtro = params['filtro'] || 'TODOS';
          this.aplicarFiltro(filtro);
        });

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error cargando trabajos:', err);
        this.isLoading.set(false);
      }
    });
  }

  aplicarFiltro(filtro: string) {
    switch (filtro) {
      case 'APP':
        this.tituloPagina.set('Trabajos de la App');
        this.trabajosVisibles.set(
          this.todosLosTrabajos.filter(t => t.tipo === 'APP')
        );
        break;

      case 'EXTERNO':
        this.tituloPagina.set('Trabajos Externos');
        this.trabajosVisibles.set(
          this.todosLosTrabajos.filter(t => t.tipo === 'EXTERNO')
        );
        break;

      default:
        this.tituloPagina.set('Todos mis Trabajos');
        this.trabajosVisibles.set(this.todosLosTrabajos);
        break;
    }
  }
}
