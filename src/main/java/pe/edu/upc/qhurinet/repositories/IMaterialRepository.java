package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.Material;

import java.util.List;
import java.util.UUID;

public interface IMaterialRepository extends JpaRepository<Material, Integer> {
    @Query(value = "\n" +
            "SELECT m.nombre, m.categoria, COALESCE(SUM(pm.cantidad), 0) AS total_kg\n" +
            " FROM material m\n" +
            " INNER JOIN publicacion_material pm ON m.id = pm.id_material\n" +
            " GROUP BY m.id, m.nombre, m.categoria\n" +
            " ORDER BY total_kg DESC\n" +
            " LIMIT 5",
            nativeQuery = true)
    public List<Object[]> top5MaterialesMasReciclados();

    @Query(value = """
        SELECT u.id, u.nombre, COALESCE(AVG(c.puntuacion), 0) AS puntuacion_promedio,
               COUNT(r.id) AS total_recolecciones
        FROM usuario u
        INNER JOIN recoleccion r ON u.id = r.id_recolector
        INNER JOIN calificacion c ON r.id = c.id_recoleccion
        WHERE u.id = :idRecolector
        AND r.estado = 'completada'
        GROUP BY u.id, u.nombre
        """, nativeQuery = true)
    List<Object[]> promedioCalificacionRecolector(@Param("idRecolector") UUID idRecolector);
}

