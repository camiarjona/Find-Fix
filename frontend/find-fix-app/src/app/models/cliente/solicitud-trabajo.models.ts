export interface MostrarSolicitudTrabajoDTO {
  id: number;
  fechaCreacion: string;
  estado: string;
  nombreEspecialista: string;
  apellidoEspecialista: string;
  descripcion: string;
  fotoUrlEspecialista?: string;
}

export interface BuscarSolicitudDTO {
  desde?: string; // Formato 'YYYY-MM-DD'
  hasta?: string;
  emailEspecialista?: string;
  estado?: string;
}
