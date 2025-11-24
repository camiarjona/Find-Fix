export interface OficioModel {
  id: number;
  nombre: string;
}

export interface OficioToDelete {
  id: number | null;
  nombre: string | null;
}

export interface messageConfirm {
    visible: boolean;
    message: string;
    type: 'success' | 'error';
}
