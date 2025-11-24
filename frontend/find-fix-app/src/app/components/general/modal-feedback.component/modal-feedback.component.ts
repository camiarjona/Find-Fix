import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-feedback',
  imports: [],
  templateUrl: './modal-feedback.component.html',
  styleUrl: './modal-feedback.component.css',
})
export class ModalFeedbackComponent {
  @Input() titulo: string = '¡Éxito!';
  @Input() mensaje: string = 'Operación realizada correctamente.';
  @Input() tipo: 'success' | 'error' = 'success'; // Para cambiar el color del icono

  @Output() cerrar = new EventEmitter<void>();
}
