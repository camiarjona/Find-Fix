import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-admin-dialog-confirm',
  imports: [],
  template: `
    <div class="modal-overlay">
      <div class="dialog-card">
        <div class="dialog-header">
          <h3>Confirmar Eliminación</h3>
        </div>
        <div class="dialog-body">
          <p>¿Estás seguro de que quieres eliminar <strong>"{{ itemName }}"</strong> de la lista?</p>
          <p class="warning-text">Esta acción no se puede deshacer.</p>
        </div>
        <div class="dialog-footer">
          <button class="cta-button-cancel" (click)="confirm.emit(false)">
            Cancelar
          </button>
          <button class="cta-button delete-confirm-button" (click)="confirm.emit(true)">
            Eliminar
          </button>
        </div>
      </div>
    </div>
  `,
  styleUrl: './admin-dialog-confirm.css',
})
export class AdminDialogConfirm {
  @Input() itemName: string = 'este elemento';
  @Output() confirm = new EventEmitter<boolean>();
}
