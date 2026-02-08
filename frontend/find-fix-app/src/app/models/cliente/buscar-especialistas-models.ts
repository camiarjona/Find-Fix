export interface EspecialistaDTO {
  nombre: string;
  apellido: string;
  ciudad: string;
  oficios: string[];
  calificacionPromedio: number;
  email: string;
  imagen?: string;
  latitud?: number;
  longitud?: number;
}

export interface FiltroEspecialistasDTO {
  oficio?: string;
  ciudad?: string;
  minCalificacion?: number;
  email?: string;
  latitud?: number;
  longitud?: number;
}

export interface CrearSolicitudTrabajoDTO {
  descripcion: string;
  emailEspecialista: string;
}
