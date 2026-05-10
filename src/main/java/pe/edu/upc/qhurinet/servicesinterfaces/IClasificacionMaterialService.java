package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.dtos.ClasificacionMaterialDTO;

import java.util.List;

public interface IClasificacionMaterialService {
    List<ClasificacionMaterialDTO> clasificar(String texto);
}
