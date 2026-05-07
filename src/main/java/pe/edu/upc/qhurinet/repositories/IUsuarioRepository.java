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

    @Query(value = """
        SELECT u.id, u.nombre, u.email, u.telefono, u.foto_url, u.descripcion,
               u.disponible, u.verificado, u.puntos_totales, u.nivel_participacion,
               COALESCE(AVG(c.puntuacion), 0) AS puntuacion_promedio,
               COUNT(c.id) AS total_valoraciones,
               COUNT(DISTINCT r.id) AS total_recolecciones
        FROM usuario u
        LEFT JOIN recoleccion r ON u.id = r.id_recolector AND r.estado = 'completada'
        LEFT JOIN calificacion c ON r.id = c.id_recoleccion
        WHERE u.id = :idRecolector
        GROUP BY u.id, u.nombre, u.email, u.telefono, u.foto_url, u.descripcion,
                 u.disponible, u.verificado, u.puntos_totales, u.nivel_participacion
        """, nativeQuery = true)
    List<Object[]> perfilRecolector(@Param("idRecolector") UUID idRecolector);

    @Query(value = """
        SELECT c.id, c.puntuacion, c.comentario, c.created_at, autor.nombre
        FROM calificacion c
        INNER JOIN recoleccion r ON c.id_recoleccion = r.id
        INNER JOIN usuario autor ON c.id_autor = autor.id
        WHERE r.id_recolector = :idRecolector
        AND c.comentario IS NOT NULL
        AND TRIM(c.comentario) <> ''
        ORDER BY c.created_at DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> comentariosRecolector(@Param("idRecolector") UUID idRecolector);

    @Query(value = """
        SELECT u.id, u.nombre, u.puntos_totales, u.nivel_participacion,
               (SELECT COUNT(*) FROM publicacion p WHERE p.id_usuario = u.id) AS publicaciones,
               (SELECT COUNT(*)
                FROM recoleccion r
                INNER JOIN publicacion p ON r.id_publicacion = p.id
                WHERE p.id_usuario = u.id AND r.estado = 'completada') AS entregas_como_emisor,
               (SELECT COUNT(*)
                FROM recoleccion r
                WHERE r.id_recolector = u.id AND r.estado = 'completada') AS recojos_como_recolector,
               (SELECT COALESCE(SUM(pm.cantidad), 0)
                FROM publicacion p
                INNER JOIN publicacion_material pm ON p.id = pm.id_publicacion
                INNER JOIN recoleccion r ON r.id_publicacion = p.id
                WHERE (p.id_usuario = u.id OR r.id_recolector = u.id)
                AND r.estado = 'completada') AS kg_reciclados,
               (SELECT COUNT(*)
                FROM recoleccion r
                INNER JOIN publicacion p ON r.id_publicacion = p.id
                WHERE (p.id_usuario = u.id OR r.id_recolector = u.id)
                AND r.incidencia_descripcion IS NOT NULL) AS incidencias
        FROM usuario u
        WHERE u.id = :idUsuario
        """, nativeQuery = true)
    List<Object[]> estadisticasResumenUsuario(@Param("idUsuario") UUID idUsuario);
}
