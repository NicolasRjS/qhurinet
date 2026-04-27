package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Recoleccion;

import java.util.UUID;

public interface IRecoleccionRepository extends JpaRepository<Recoleccion, UUID> {
}
