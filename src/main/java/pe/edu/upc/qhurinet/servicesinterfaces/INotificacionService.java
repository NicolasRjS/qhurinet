package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Notificacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface INotificacionService {
    List<Notificacion> list();
    Notificacion insert(Notificacion n);
    Optional<Notificacion> listId(UUID id);
    void update(Notificacion n);
    void delete(UUID id);
    List<Notificacion> listByUsuario(UUID idUsuario);
    List<Notificacion> listNoLeidasByUsuario(UUID idUsuario);
    List<Notificacion> listByEstado(String estado);
    boolean existsByUsuarioTipoTitulo(UUID idUsuario, String tipo, String titulo);
}
