package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.MetodoPago;

import java.util.List;
import java.util.UUID;

public interface IMetodoPagoRepository extends JpaRepository<MetodoPago, UUID> {
    List<MetodoPago> findByUsuarioIdOrderByCreatedAtDesc(UUID idUsuario);
    List<MetodoPago> findByUsuarioIdAndActivoTrueOrderByCreatedAtDesc(UUID idUsuario);
}
