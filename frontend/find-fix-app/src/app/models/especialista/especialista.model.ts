import { EstadoSolicitud, EstadoTrabajo } from "../enums/enums.model";

export interface SolicitudRecibida {
  id: number;
  descripcion: string;
  fecha: string | Date;
  estado: EstadoSolicitud | string;
  idCliente: number;
  nombreCliente: string;
}


export interface TrabajoApp {
  id: number;
  nombreCliente: string;
  titulo: string;
  descripcion: string;
  estado: EstadoTrabajo | string;
  presupuesto: number;
  fechaInicio: Date | string;
  fechaFin: Date | string;
}


export interface TrabajoExterno{
  id: number;
  nombreCliente: string;
  titulo: string;
  fechaInicio: Date | string;
  fechaFin: Date | string;
  estado: EstadoTrabajo | string;
  descripcion: string;
  presupuesto: number;
}


export interface TrabajoEspecialista {
  id: number;
  titulo: string;
  estado: EstadoTrabajo | string;
  fechaInicio: Date | string;
  fechaFin?: Date | string;
  nombreCliente: string;
  descripcion : string;
  presupuesto: number;
  tipo: 'APP' | 'EXTERNO';
}

export interface OficioEspecialista {
  id: number;
  nombre: string;
}

export interface ActualizarOficios{
  agregar: string[];
  eliminar: string[];
}


export interface PerfilEspecialista {
  nombre: string;
  apellido: string;
  email: string;
  ciudad: string;
  telefono: string;
  descripcion: string;
  oficios: OficioEspecialista[];
  calificacionPromedio : number;
  dni : number;
}

export interface ResenaEspecialista {
  resenaId : number;
  puntuacion : number;
  comentario : string;
  nombreCliente : string;

}
