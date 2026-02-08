export interface NotificacionModels {
  id: number;
  titulo: string;
  mensaje: string;
  leida: boolean;
  fechaCreacion: string; // O Date
  tipo?: string; // 'INFO', 'ALERTA', 'EXITO' (opcional para colores)
}
