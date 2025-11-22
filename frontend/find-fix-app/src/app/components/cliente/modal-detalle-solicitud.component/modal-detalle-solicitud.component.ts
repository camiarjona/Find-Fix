import { CommonModule, DatePipe } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FichaCompletaSolicitud } from '../../../models/cliente/solicitud-especialista.model';

@Component({
  selector: 'app-modal-detalle-solicitud',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './modal-detalle-solicitud.component.html',
  styleUrl: './modal-detalle-solicitud.component.css',
})
export class ModalDetalleSolicitud {

  @Input() solicitud: FichaCompletaSolicitud | null = null;

  @Output() cerrar = new EventEmitter<void>();

}
