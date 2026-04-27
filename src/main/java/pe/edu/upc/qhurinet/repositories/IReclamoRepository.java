package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Reclamo;

import java.util.UUID;

public interface IReclamoRepository extends JpaRepository<Reclamo, UUID> {
}
