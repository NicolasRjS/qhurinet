package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.SoporteContacto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISoporteContactoService {
    List<SoporteContacto> list();
    SoporteContacto insert(SoporteContacto c);
    Optional<SoporteContacto> listId(UUID id);
    void update(SoporteContacto c);
    void delete(UUID id);
    List<SoporteContacto> listActivos();
}
