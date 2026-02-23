/*Estados para la solicitud de especialista */

export type EstadoSolicitud = 'Pendiente' | 'Aceptado' | 'Rechazado';

export type EstadoTrabajo = 'Creado' | 'En proceso' | 'En revision' | 'Finalizado';

// Define las direcciones posibles de ordenamiento
export type DireccionOrden = 'asc' | 'desc';

// Define los criterios globales
export type CriterioOrden = 'FECHA' | 'CALIFICACION' | 'DISTANCIA' | 'ALFABETICO' | 'ESTADO' | 'ROL' | 'EMAIL';


export interface ConfiguracionOrden {
  criterio: CriterioOrden;
  direccion: DireccionOrden;
}
