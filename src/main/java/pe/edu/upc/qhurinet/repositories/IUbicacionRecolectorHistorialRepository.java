package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.UbicacionRecolectorHistorial;

import java.util.List;
import java.util.UUID;

public interface IUbicacionRecolectorHistorialRepository extends JpaRepository<UbicacionRecolectorHistorial, UUID> {
    List<UbicacionRecolectorHistorial> findByRecoleccionIdOrderByCreatedAtDesc(UUID idRecoleccion);
    void deleteByRecoleccionId(UUID idRecoleccion);
}
