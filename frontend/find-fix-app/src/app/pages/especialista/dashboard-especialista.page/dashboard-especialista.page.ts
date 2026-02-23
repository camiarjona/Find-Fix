import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { EspecialistaService } from '../../../services/especialista/especialista.service';
import { ResenaEspecialista, TrabajoEspecialista } from '../../../models/especialista/especialista.model';

@Component({
  selector: 'app-dashboard-especialista.page',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './dashboard-especialista.page.html',
  styleUrl: './dashboard-especialista.page.css',
})
export class DashboardEspecialistaPage {
  private router = inject(Router);
  private authService = inject(AuthService);
  private especialistaService = inject(EspecialistaService);

  nombreUsuario = computed(() => this.authService.currentUser()?.nombre || 'Campeón');
  fechaActual = new Date();

  //SEÑALES PARA LA VISTA
  solicitudesPendientes = signal(0);
  trabajosEnCurso = signal(0);
  trabajosCompletados = signal(0);
  calificacionPromedio = signal(0);
  ingresosMes = signal(0);
  totalResenas = signal(0);

  ultimasResenas = signal<ResenaEspecialista[]>([]);

  ngOnInit() {
    this.cargarResenas();
  }


  cargarResenas() {
    // 1. SOLICITUDES
    this.especialistaService.getSolicitudesRecibidas().subscribe({
      next: (solicitudes) => {
        const pendientes = solicitudes.filter(s => s.estado?.toUpperCase() === 'PENDIENTE').length;
        const rechazadas = solicitudes.filter(s => s.estado?.toUpperCase() === 'RECHAZADO').length;

        this.solicitudesPendientes.set(pendientes);

        this.datosSolicitudes = {
          ...this.datosSolicitudes,
          datasets: [{
            ...this.datosSolicitudes.datasets[0],
            data: [pendientes, rechazadas]
          }]
        };
      },
      error: (err) => console.error('Error solicitudes', err)
    });

    // 2. TRABAJOS E INGRESOS
    this.especialistaService.getMisTrabajos().subscribe({
      next: (trabajos) => {
        // Trabajos en curso
        const enCurso = trabajos.filter(t => t.estado?.toString() === 'En proceso').length;
        this.trabajosEnCurso.set(enCurso);

        // Actualizar Gráfico Dona En Curso
        this.datosEnCurso = {
          ...this.datosEnCurso,
          datasets: [{
            ...this.datosEnCurso.datasets[0],
            data: [enCurso, this.solicitudesPendientes()]
          }]
        };

        // Trabajos Finalizados
        const finalizados = trabajos.filter(t => t.estado?.toUpperCase() === 'FINALIZADO');
        this.trabajosCompletados.set(finalizados.length);

        // Actualizar Gráfico Barras Historial
        const historial = this.calcularHistorialMeses(finalizados);
        this.datosCompletados = {
          ...this.datosCompletados,
          datasets: [{
            ...this.datosCompletados.datasets[0],
            data: historial
          }]
        };

        // Ingresos
        const totalIngresos = finalizados.reduce((sum, t) => sum + (t.presupuesto || 0), 0);
        this.ingresosMes.set(totalIngresos);

        // Actualizar Gráfico Ingresos (Lineas)
        this.datosIngresos = {
          ...this.datosIngresos,
          datasets: [{
            ...this.datosIngresos.datasets[0],
            data: [
              totalIngresos * 0.1,
              totalIngresos * 0.4,
              totalIngresos * 0.2,
              totalIngresos * 0.3
            ]
          }]
        };
      },
      error: (err) => console.error('Error trabajos', err)
    });

    // 3. RESEÑAS Y CALIFICACIÓN
    this.especialistaService.getMisResenas().subscribe({
      next: (resenas) => {
        this.totalResenas.set(resenas.length);
        this.ultimasResenas.set(resenas.slice(0, 5));

        if (resenas.length > 0) {
          const suma = resenas.reduce((acc, r) => acc + r.puntuacion, 0);
          const promedio = suma / resenas.length;
          this.calificacionPromedio.set(parseFloat(promedio.toFixed(1)));

          const cincoEstrellas = resenas.filter(r => r.puntuacion >= 4.5).length;
          const cuatroEstrellas = resenas.filter(r => r.puntuacion >= 3.5 && r.puntuacion < 4.5).length;
          const tresOMenos = resenas.filter(r => r.puntuacion < 3.5).length;

          this.datosCalificacion = {
            ...this.datosCalificacion,
            datasets: [{
              ...this.datosCalificacion.datasets[0],
              data: [cincoEstrellas, cuatroEstrellas, tresOMenos]
            }]
          };

          const historialResenas = this.calcularHistorialMesesResenas(resenas);

          this.datosResenas = {
            ...this.datosResenas,
            datasets: [{
              ...this.datosResenas.datasets[0],
              data: historialResenas
            }]
          };
        }
      },
      error: (err) => console.error('Error al cargar reseñas', err)
    });
  }

  // Actualiza los datos de un gráfico
  private actualizarGrafico(datasetConfig: ChartConfiguration['data'], nuevosDatos: number[]) {
    datasetConfig.datasets[0].data = nuevosDatos;
  }

  private calcularHistorialMeses(trabajos: TrabajoEspecialista[]): number[] {
    const hoy = new Date();
    const mesActual = hoy.getMonth();
    const conteo = [0, 0, 0];

    trabajos.forEach(t => {
      if (t.fechaFin) {
        const fechaFin = new Date(t.fechaFin);
        const mesFin = fechaFin.getMonth();

        if (mesFin === mesActual) conteo[2]++;
        else if (mesFin === mesActual - 1) conteo[1]++;
        else if (mesFin === mesActual - 2) conteo[0]++;
      }
    });
    return conteo;
  }

  // Método auxiliar para contar reseñas por mes (Últimos 3 meses)
  private calcularHistorialMesesResenas(resenas: any[]): number[] {
    const hoy = new Date();
    const mesActual = hoy.getMonth();
    const conteo = [0, 0, 0];

    resenas.forEach(r => {

      const fechaStr = r.fecha || r.fechaResena;

      if (fechaStr) {
        const fechaResena = new Date(fechaStr);
        const mesResena = fechaResena.getMonth();

        if (fechaResena.getFullYear() === hoy.getFullYear()) {
          if (mesResena === mesActual) conteo[2]++;
          else if (mesResena === mesActual - 1) conteo[1]++;
          else if (mesResena === mesActual - 2) conteo[0]++;
        }
      }
    });
    return conteo;
  }

  //CONFIGURACIÓN GRÁFICOS
  opcionesComunes: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false }, tooltip: { enabled: true } },
    scales: { x: { display: false }, y: { display: false } },
    layout: { padding: 0 }
  };

  public opcionesDona: ChartOptions<any> = { ...this.opcionesComunes, cutout: '75%' };
  public opcionesBarras: ChartOptions = { ...this.opcionesComunes, elements: { bar: { borderRadius: 4 } } };
  public opcionesLineas: ChartOptions = { ...this.opcionesComunes, elements: { line: { tension: 0.4, borderWidth: 2 }, point: { radius: 0, hoverRadius: 4 } } };

  //DATASETS INICIALES
  public datosSolicitudes: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['Pendientes', 'Rechazadas'],
    datasets: [{ data: [0, 0], backgroundColor: ['#3182ce', '#e2e8f0'], borderWidth: 0 }]
  };

  public datosEnCurso: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['Activos', 'Pendientes'],
    datasets: [{ data: [0, 0], backgroundColor: ['#dd6b20', '#e2e8f0'], borderWidth: 0 }]
  };

  public datosCompletados: ChartConfiguration<'bar'>['data'] = {
    labels: this.obtenerUltimos3MesesLabels(),
    datasets: [{ data: [0, 0, 0], backgroundColor: '#38a169' }]
  };

  public datosCalificacion: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['5★', '4★', '3★-'],
    datasets: [{ data: [0, 0, 0], backgroundColor: ['#d69e2e', '#ecc94b', '#fefcbf'], borderWidth: 0 }]
  };

  public datosIngresos: ChartConfiguration<'line'>['data'] = {
    labels: ['S1', 'S2', 'S3', 'S4'],
    datasets: [{ data: [0, 0, 0, 0], borderColor: '#059669', backgroundColor: 'rgba(16, 185, 129, 0.2)', fill: true }]
  };

  public datosResenas: ChartConfiguration<'bar'>['data'] = {
    labels: this.obtenerUltimos3MesesLabels(),
    datasets: [{ data: [0, 0, 0], backgroundColor: '#ec4899', borderRadius: 4 }]
  };

  private obtenerUltimos3MesesLabels(): string[] {
    const meses = [];
    const opciones: Intl.DateTimeFormatOptions = { month: 'short' };
    for (let i = 2; i >= 0; i--) {
      const d = new Date();
      d.setMonth(d.getMonth() - i);
      meses.push(new Intl.DateTimeFormat('es-ES', opciones).format(d));
    }
    return meses;
  }

  irASolicitudes() { this.router.navigate(['/especialista/solicitudes']); }
  irATrabajos() { this.router.navigate(['/especialista/mis-trabajos']); }
}
