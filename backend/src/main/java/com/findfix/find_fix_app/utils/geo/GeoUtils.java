package com.findfix.find_fix_app.utils.geo;
import org.springframework.stereotype.Component;

@Component
public class GeoUtils {

    // Radio de la Tierra en kilómetros (constante)
    private static final int RADIO_TIERRA_KM = 6371;

    // Calcula la distancia en kilómetros entre dos coordenadas geográficas usando la fórmula de Haversine.

    public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // 1. Convertir grados a radianes
        // Las funciones trigonométricas (seno, coseno) requieren radianes.
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // 2. Aplicar la fórmula de Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        // Parte C: Calcula la distancia angular en radianes.
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        // 3. Calcular distancia final
        // Multiplicamos el ángulo por el radio de la Tierra.
        return RADIO_TIERRA_KM * c;
    }
}