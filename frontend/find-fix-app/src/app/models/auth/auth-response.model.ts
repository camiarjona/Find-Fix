export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  email: string;
  nombre: string;
  apellido: string;
  activo: boolean;
  id: number;
  roles: string[];
}
