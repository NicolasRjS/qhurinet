package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Notificacion;

import java.util.List;
import java.util.UUID;

public interface INotificacionRepository extends JpaRepository<Notificacion, UUID> {
    List<Notificacion> findByUsuarioIdOrderByCreatedAtDesc(UUID idUsuario);
    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc(UUID idUsuario);
    List<Notificacion> findByEstadoOrderByCreatedAtAsc(String estado);
    boolean existsByUsuarioIdAndTipoAndTitulo(UUID idUsuario, String tipo, String titulo);
}
