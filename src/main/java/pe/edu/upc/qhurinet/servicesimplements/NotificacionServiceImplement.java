package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Notificacion;
import pe.edu.upc.qhurinet.repositories.INotificacionRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.INotificacionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificacionServiceImplement implements INotificacionService {
    @Autowired
    private INotificacionRepository nR;

    @Override
    public List<Notificacion> list() {
        return nR.findAll();
    }

    @Override
    public Notificacion insert(Notificacion n) {
        return nR.save(n);
    }

    @Override
    public Optional<Notificacion> listId(UUID id) {
        return nR.findById(id);
    }

    @Override
    public void update(Notificacion n) {
        nR.save(n);
    }

    @Override
    public void delete(UUID id) {
        nR.deleteById(id);
    }

    @Override
    public List<Notificacion> listByUsuario(UUID idUsuario) {
        return nR.findByUsuarioIdOrderByCreatedAtDesc(idUsuario);
    }

    @Override
    public List<Notificacion> listNoLeidasByUsuario(UUID idUsuario) {
        return nR.findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc(idUsuario);
    }

    @Override
    public List<Notificacion> listByEstado(String estado) {
        return nR.findByEstadoOrderByCreatedAtAsc(estado);
    }

    @Override
    public boolean existsByUsuarioTipoTitulo(UUID idUsuario, String tipo, String titulo) {
        return nR.existsByUsuarioIdAndTipoAndTitulo(idUsuario, tipo, titulo);
    }
}
