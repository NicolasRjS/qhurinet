package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.dtos.DistanciaDTO;
import pe.edu.upc.qhurinet.dtos.MatrizDistanciaRequestDTO;
import pe.edu.upc.qhurinet.dtos.MatrizDistanciaResponseDTO;
import pe.edu.upc.qhurinet.dtos.PuntoRutaDTO;
import pe.edu.upc.qhurinet.servicesinterfaces.IGeolocalizacionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeolocalizacionServiceImplement implements IGeolocalizacionService {
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double DEFAULT_SPEED_KMH = 20.0;

    @Override
    public double distanciaKm(double latOrigen, double lngOrigen, double latDestino, double lngDestino) {
        double dLat = Math.toRadians(latDestino - latOrigen);
        double dLng = Math.toRadians(lngDestino - lngOrigen);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latOrigen)) * Math.cos(Math.toRadians(latDestino))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return redondear(EARTH_RADIUS_KM * c);
    }

    @Override
    public int tiempoEstimadoMin(double distanciaKm) {
        return (int) Math.ceil((distanciaKm / DEFAULT_SPEED_KMH) * 60.0);
    }

    @Override
    public DistanciaDTO distancia(double latOrigen, double lngOrigen, double latDestino, double lngDestino) {
        double distanciaKm = distanciaKm(latOrigen, lngOrigen, latDestino, lngDestino);
        DistanciaDTO dto = new DistanciaDTO();
        dto.setLatOrigen(latOrigen);
        dto.setLngOrigen(lngOrigen);
        dto.setLatDestino(latDestino);
        dto.setLngDestino(lngDestino);
        dto.setDistanciaKm(distanciaKm);
        dto.setTiempoEstimadoMin(tiempoEstimadoMin(distanciaKm));
        dto.setProveedor("qhuri-mock-haversine");
        return dto;
    }

    @Override
    public MatrizDistanciaResponseDTO matriz(MatrizDistanciaRequestDTO request) {
        if (request == null || request.getOrigenes() == null || request.getOrigenes().isEmpty()
                || request.getDestinos() == null || request.getDestinos().isEmpty()) {
            throw new IllegalArgumentException("Origenes y destinos son obligatorios");
        }

        List<List<DistanciaDTO>> matriz = new ArrayList<>();
        for (PuntoRutaDTO origen : request.getOrigenes()) {
            validarPunto(origen);
            List<DistanciaDTO> fila = new ArrayList<>();
            for (PuntoRutaDTO destino : request.getDestinos()) {
                validarPunto(destino);
                fila.add(distancia(origen.getLatitud(), origen.getLongitud(), destino.getLatitud(), destino.getLongitud()));
            }
            matriz.add(fila);
        }

        MatrizDistanciaResponseDTO response = new MatrizDistanciaResponseDTO();
        response.setProveedor("qhuri-mock-haversine");
        response.setMatriz(matriz);
        return response;
    }

    private void validarPunto(PuntoRutaDTO punto) {
        if (punto == null || punto.getLatitud() == null || punto.getLongitud() == null) {
            throw new IllegalArgumentException("Cada punto debe tener latitud y longitud");
        }
    }

    private double redondear(double value) {
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}
