package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.PublicacionMaterialId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IPublicacionMaterialRepository extends JpaRepository<PublicacionMaterial, PublicacionMaterialId> {
    List<PublicacionMaterial> findByPublicacionId(UUID idPublicacion);

    @Query(value = """
        SELECT COALESCE(SUM(pm.cantidad * m.puntos_por_kg), 0)
        FROM publicacion_material pm
        INNER JOIN material m ON pm.id_material = m.id
        WHERE pm.id_publicacion = :idPublicacion
        """, nativeQuery = true)
    BigDecimal puntosPorPublicacion(@Param("idPublicacion") UUID idPublicacion);
}
