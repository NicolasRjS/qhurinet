package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.UbicacionRecolectorHistorial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUbicacionRecolectorHistorialService {
    List<UbicacionRecolectorHistorial> list();
    UbicacionRecolectorHistorial insert(UbicacionRecolectorHistorial u);
    Optional<UbicacionRecolectorHistorial> listId(UUID id);
    void update(UbicacionRecolectorHistorial u);
    void delete(UUID id);
    List<UbicacionRecolectorHistorial> listByRecoleccion(UUID idRecoleccion);
    void deleteByRecoleccion(UUID idRecoleccion);
}
