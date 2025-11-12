
export interface UserProfile {
  nombre: string;
  apellido: string;
  email: string;
  ciudad: string;
  telefono: string;
  roles: string[];
}

export interface LoginCredentials {
  email: string;
  password: string;
}
