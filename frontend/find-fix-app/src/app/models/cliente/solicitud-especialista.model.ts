import { EstadoSolicitud } from "../enums/enums.model";

export interface MandarSolicitud{
  motivo : string;
}

export interface MostrarSolicitud{
  seId: number;
  fechaSolicitud: Date;
  estado: EstadoSolicitud;
  email: string;
  respuesta: string;
}

export interface FichaCompletaSolicitud{
  seId: number;
  fechaSolicitud: Date;
  fechaResolucion: Date;
  estado: EstadoSolicitud;
  motivo: string;
  email: string;
  respuesta: string;
}

export interface SolicitudFilter {
  motivo?: string;
  estado?: string;
  fechaDesde?: string;
  fechaHasta?: string;
}

export interface FilterChip {
  key: keyof SolicitudFilter;
  label: string;
  value: any;
}
