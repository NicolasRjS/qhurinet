package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Publicacion;

import java.util.UUID;

public interface IPublicacionRepository extends JpaRepository<Publicacion, UUID> {
}
