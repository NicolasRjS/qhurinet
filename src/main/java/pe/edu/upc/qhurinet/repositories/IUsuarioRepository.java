package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.Usuario;

import java.util.List;
import java.util.UUID;

public interface IUsuarioRepository extends JpaRepository<Usuario, UUID> {
    Usuario findOneByUsername(String username);

    @Query(value = """
        SELECT TO_CHAR(r.fecha_completada, 'YYYY-MM') AS mes,
        COALESCE(SUM(pm.cantidad), 0) AS total_kg
        FROM recoleccion r
        INNER JOIN publicacion p ON r.id_publicacion = p.id
        INNER JOIN publicacion_material pm ON p.id = pm.id_publicacion
        WHERE r.estado = 'completada'
        AND (r.id_recolector = :idUsuario OR p.id_usuario = :idUsuario)
        AND r.fecha_completada >= (CURRENT_DATE - INTERVAL '6 months')
        GROUP BY EXTRACT(YEAR FROM r.fecha_completada),
                 EXTRACT(MONTH FROM r.fecha_completada),
                 TO_CHAR(r.fecha_completada, 'YYYY-MM')
        ORDER BY EXTRACT(YEAR FROM r.fecha_completada) ASC,
                 EXTRACT(MONTH FROM r.fecha_completada) ASC
        """, nativeQuery = true)
    List<Object[]> kgRecicladosPorMes(@Param("idUsuario") UUID idUsuario);


    @Query(value = """
        SELECT u.id, u.nombre, u.puntos_totales, u.nivel_participacion
        FROM usuario u
        ORDER BY u.puntos_totales DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> rankingUsuariosPorPuntos();
}
