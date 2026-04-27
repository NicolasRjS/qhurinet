package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Material;

public interface IMaterialRepository extends JpaRepository<Material, Integer> {
}
