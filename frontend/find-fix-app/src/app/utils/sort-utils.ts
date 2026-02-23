import { DireccionOrden } from "../models/enums/enums.model";


export function ordenarDinamicamente<T>(
  datos: T[],
  propiedad: string,
  direccion: DireccionOrden = 'asc'
): T[] {

  return [...datos].sort((a: any, b: any) => {
    let valorA = obtenerValorPropiedad(a, propiedad);
    let valorB = obtenerValorPropiedad(b, propiedad);


    if (typeof valorA === 'string') valorA = valorA.toLowerCase();
    if (typeof valorB === 'string') valorB = valorB.toLowerCase();


    if (valorA < valorB) {
      return direccion === 'asc' ? -1 : 1;
    }
    if (valorA > valorB) {
      return direccion === 'asc' ? 1 : -1;
    }
    return 0;
  });
}

/**
 * Función auxiliar para navegar en objetos anidados
 * Ejemplo: si la ruta es 'usuario.rol.nombre', entra nivel por nivel
 */
function obtenerValorPropiedad(objeto: any, ruta: string | number | symbol): any {
  return String(ruta)
    .split('.')
    .reduce((acumulado, parte) => acumulado && acumulado[parte], objeto);
}
