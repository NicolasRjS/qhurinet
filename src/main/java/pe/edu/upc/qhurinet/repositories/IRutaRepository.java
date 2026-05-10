package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Ruta;

import java.util.List;
import java.util.UUID;

public interface IRutaRepository extends JpaRepository<Ruta, UUID> {
    List<Ruta> findByUsuarioIdOrderByCreatedAtDesc(UUID idUsuario);
    List<Ruta> findByUsuarioIdAndFavoritaTrueOrderByCreatedAtDesc(UUID idUsuario);
}
