export interface TrabajoExternoDTO {
  nombreCliente: string;
  titulo: string;
  descripcion: string;
  estado: string;
  presupuesto: number;
  fechaInicio: string;
  fechaFin: string;
}

export interface CrearTrabajoExternoDTO {
  nombreCliente: string;
  titulo: string;
  descripcion: string;
  presupuesto: number;
}

export interface ModificarTrabajoExternoDTO {
  nombreCliente?: string;
  titulo?: string;
  descripcion?: string;
  presupuesto?: number;
}

export interface BuscarTrabajoExternoDTO {
  titulo?: string;
  estado?: string;
  id?: number;
  desde?: string; // YYYY-MM-DD
  hasta?: string; // YYYY-MM-DD
}
