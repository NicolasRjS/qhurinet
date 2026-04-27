package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.PublicacionMaterialId;

public interface IPublicacionMaterialRepository extends JpaRepository<PublicacionMaterial, PublicacionMaterialId> {
}
