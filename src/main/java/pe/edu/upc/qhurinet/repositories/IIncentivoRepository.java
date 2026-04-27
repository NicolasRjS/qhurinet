package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Incentivo;

import java.util.UUID;

public interface IIncentivoRepository extends JpaRepository<Incentivo, UUID> {
}
