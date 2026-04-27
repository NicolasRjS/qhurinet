package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Calificacion;

import java.util.UUID;

public interface ICalificacionRepository extends JpaRepository<Calificacion, UUID> {
}
