import { ChangeDetectorRef, Component, ElementRef, HostListener, inject, Input, OnInit, OnDestroy } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { NotificacionModels } from '../../../models/general/notificacion.models';
import { NotificacionService } from '../../../services/notificacion/notificacion-service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-notificacion-component',
  imports: [CommonModule,RouterModule],
  templateUrl: './notificacion-component.html',
  styleUrl: './notificacion-component.css',
})
export class NotificacionComponent implements OnInit {
private notificationService = inject(NotificacionService);
  private elementRef = inject(ElementRef);
  private cdr = inject(ChangeDetectorRef);
  @Input() rolActual: string = 'CLIENTE';
  private pollingSubscription: Subscription | undefined; // <--- Variable para controlar el tiempo
  notificaciones: NotificacionModels[] = [];
  cantidadNoLeidas: number = 0;
  esVisible: boolean = false;

  ngOnInit(): void {

    this.cargarNotificaciones();
    this.pollingSubscription = interval(10000).subscribe(() => {
      if (!this.esVisible) {
        this.cargarNotificaciones();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe(); // Apagamos el reloj
    }
  }
  cargarNotificaciones() {
    this.notificationService.obtenerMisNotificaciones(this.rolActual).subscribe({
      next: (response) => {
        this.notificaciones = response.data.sort((a, b) =>
          new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
        );


        this.calcularNoLeidas();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error cargando notificaciones', err)
    });
  }

  calcularNoLeidas() {
    this.cantidadNoLeidas = this.notificaciones.filter(n => !n.leida).length;
  }

  toggleMenu() {
    this.esVisible = !this.esVisible;
  }

  marcarComoLeida(noti: NotificacionModels) {
    if (!noti.leida) {
      this.notificationService.marcarComoLeida(noti.id).subscribe({
        next: () => {

          noti.leida = true;
          this.calcularNoLeidas();
        },
        error: (err) => console.error('Error al marcar leída', err)
      });
    }
  }


  @HostListener('document:click', ['$event'])
  clickFuera(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.esVisible = false;
    }
}}
