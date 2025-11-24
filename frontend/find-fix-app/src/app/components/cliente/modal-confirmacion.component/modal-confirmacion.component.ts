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
@Input() titulo: string = '';
  @Input() mensaje: string = '';
  @Input() textoCancelar: string = 'Cancelar';
  @Input() textoConfirmar: string = 'Confirmar';
  @Input() tipo: 'pregunta' | 'exito' = 'pregunta';

  @Input() mostrarBotonCancelar: boolean = false;

  @Output() cancelar = new EventEmitter<void>();
  @Output() confirmar = new EventEmitter<void>();
}
