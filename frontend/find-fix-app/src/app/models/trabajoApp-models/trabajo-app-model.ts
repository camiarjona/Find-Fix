export interface VisualizarTrabajoAppCliente {
  id: number;
  nombreEspecialista: string;
  descripcion: string;
  estado: string; // ACA DEBERIAMOS DE USAR EL ENUM¿?¿¿?¿?
  presupuesto: number;
  fechaInicio: string;
  fechaFin: string;
}

export interface VisualizarTrabajoAppEspecialista {
  id: number;
  nombreCliente: string;
  titulo: string;
  descripcion: string;
  estado: string;
  presupuesto: number;
  fechaInicio: string;
  fechaFin: string;
  origen?: string;
}

export interface ActualizarTrabajoApp {
  titulo?: string;
  descripcion?: string;
  presupuesto?: number;
}

export interface BuscarTrabajoApp {
  titulo?: string;
  estado?: string;
  id?: number;
  desde?: string; // Formato YYYY-MM-DD
  hasta?: string; // Formato YYYY-MM-DD
}
