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

  // --- Entradas (Inputs) ---
  @Input() titulo: string = '¡Éxito!';
  @Input() mensaje: string = 'La operación se completó correctamente.';

  // --- Salidas (Outputs) ---
  @Output() irInicio = new EventEmitter<void>();
  @Output() irHistorial = new EventEmitter<void>();
  @Output() cerrarModal = new EventEmitter<void>();
}
