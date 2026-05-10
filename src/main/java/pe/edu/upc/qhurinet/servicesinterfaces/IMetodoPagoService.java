package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.MetodoPago;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IMetodoPagoService {
    List<MetodoPago> list();
    MetodoPago insert(MetodoPago m);
    Optional<MetodoPago> listId(UUID id);
    void update(MetodoPago m);
    void delete(UUID id);
    List<MetodoPago> listByUsuario(UUID idUsuario);
    List<MetodoPago> listActivosByUsuario(UUID idUsuario);
}
