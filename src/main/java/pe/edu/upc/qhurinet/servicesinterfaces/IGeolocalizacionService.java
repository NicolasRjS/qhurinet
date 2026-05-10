package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.dtos.DistanciaDTO;
import pe.edu.upc.qhurinet.dtos.MatrizDistanciaRequestDTO;
import pe.edu.upc.qhurinet.dtos.MatrizDistanciaResponseDTO;

public interface IGeolocalizacionService {
    double distanciaKm(double latOrigen, double lngOrigen, double latDestino, double lngDestino);
    int tiempoEstimadoMin(double distanciaKm);
    DistanciaDTO distancia(double latOrigen, double lngOrigen, double latDestino, double lngDestino);
    MatrizDistanciaResponseDTO matriz(MatrizDistanciaRequestDTO request);
}
