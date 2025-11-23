import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-confirmacion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-confirmacion.component.html',
  styleUrl: './modal-confirmacion.component.css',
})
export class ModalConfirmacionComponent {

// Datos para mostrar
  @Input() titulo: string = '';
  @Input() mensaje: string = '';

  // Configuración de botones (con valores por defecto)
  @Input() textoCancelar: string = 'Cancelar';
  @Input() textoConfirmar: string = 'Confirmar';
  
  @Input() tipo: 'pregunta' | 'exito' = 'pregunta';

  // Eventos
  @Output() cancelar = new EventEmitter<void>();
  @Output() confirmar = new EventEmitter<void>();
}
