package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.dtos.RutaOptimaRequestDTO;
import pe.edu.upc.qhurinet.dtos.RutaOptimaResponseDTO;

public interface IRutaOptimaService {
    RutaOptimaResponseDTO calcular(RutaOptimaRequestDTO request);
}
