import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OficiosService } from '../../../services/admin-services/oficios-service';

@Component({
  selector: 'app-oficios-list-admin-component',
  imports: [CommonModule,],
  templateUrl: './oficios-list-admin-component.html',
  styleUrl: './oficios-list-admin-component.css',
})
export class OficiosListAdminComponent {

  private oficiosService = inject(OficiosService);
    public oficios = this.oficiosService.oficios;

    ngOnInit(): void {
        console.log("Iniciando carga de oficios..."); // Pasa este punto si la app carga.
        this.oficiosService.getOficios(); // <--- ESTO ES CRUCIAL
    }

   // public isLoading = false;

   /* // Visibilidad del modal
    public showConfirmDialog = signal(false);
    // Datos temporales para el modal
    public currentOffcioToDelete: OficioToDelete = { id: null, nombre: null };

    // 🌟 SEÑAL PARA MOSTRAR MENSAJE DE PÁGINA 🌟
    public pageMessage = signal<mensajeOficioConfirm>({
        visible: false,
        message: '',
        type: 'success',
    });



    // 1. ABRIR DIÁLOGO DE CONFIRMACIÓN
    eliminarOficio(id: number, nombre: string): void {
        this.currentOffcioToDelete = { id, nombre };
        this.showConfirmDialog.set(true);
    }

    // 2. FUNCIÓN MANEJADORA: Cierra modal y muestra mensaje
  handleDeleteConfirmation(confirmed: boolean): void {
        this.showConfirmDialog.set(false); // Cierra el modal (Paso 1)

        if (confirmed && this.currentOffcioToDelete.id !== null) {
            const { id, nombre } = this.currentOffcioToDelete;

            // Llama al servicio simulado (elimina el ítem de la Signal)
            this.oficiosService.deleteOficioSimulado(id).subscribe({
                next: () => {
                    // 🌟 Muestra el mensaje de éxito inmediatamente 🌟
                    this.displayPageMessage(`Oficio "${nombre}" eliminado correctamente.`, 'success');
                },
                error: (err) => {
                    this.displayPageMessage('Error al eliminar oficio.', 'error');
                    console.error('Fallo al eliminar (simulado):', err);
                }
            });
        }
        this.currentOffcioToDelete = { id: null, nombre: null };
    }

    // 3. 🌟 LÓGICA PARA MOSTRAR Y OCULTAR EL MENSAJE 🌟
    displayPageMessage(message: string, type: 'success' | 'error'): void {
        this.pageMessage.set({ visible: true, message, type });
        setTimeout(() => {
            // Oculta el mensaje después de 3 segundos
            this.pageMessage.set({ visible: false, message: '', type: 'success' });
        }, 3000);
    }
*/

}
