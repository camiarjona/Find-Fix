import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-alerta',
  imports: [],
  templateUrl: './modal-alerta.component.html',
  styleUrl: './modal-alerta.component.css',
})
export class ModalAlertaComponent {

  // --- Entradas (Inputs) ---
  @Input() titulo: string = 'Confirmar Acción';
  @Input() mensaje: string = '¿Estás seguro de que deseas continuar?';
  @Input() textoBotonConfirmar: string = 'Confirmar';

  // --- Salidas (Outputs) ---
  @Output() confirmar = new EventEmitter<void>();
  @Output() cancelar = new EventEmitter<void>();
}
