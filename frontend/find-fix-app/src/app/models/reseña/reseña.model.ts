
export interface CrearResenaDTO {
  puntuacion: number;
  comentario: string;
  trabajoId: number;
}

export interface MostrarResenaClienteDTO {
  resenaId: number;
  puntuacion: number;
  comentario: string;
  nombreEspecialista: string;
  fotoUrlEspecialista?: string;
}

export interface MostrarResenaEspecialistaDTO {
  resenaId: number;
  puntuacion: number;
  comentario: string;
  nombreCliente: string;
  fotoUrlCliente?: string;
}

export interface MostrarResenaDTO {
  resenaId: number;
  puntuacion: number;
  comentario: string;
  fechaInicio: Date;
  fechaFin: Date;
  estado: String;
  descripcion: String;
  presupuesto: number;
  fotoUrlCliente?: string;
}
