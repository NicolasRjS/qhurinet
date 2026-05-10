package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Publicacion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface IPublicacionRepository extends JpaRepository<Publicacion, UUID> {
    @Query(value = "\n" +
            "SELECT p.id, p.titulo, p.direccion_referencia, p.latitud, p.longitud\n" +
            " FROM publicacion p\n" +
            " WHERE p.estado = 'activa'\n" +
            " AND (p.titulo ILIKE CONCAT('%', :texto, '%')\n" +
            " OR p.direccion_referencia ILIKE CONCAT('%', :texto, '%'))\n" +
            " ORDER BY p.created_at DESC",
            nativeQuery = true)
    public List<Object[]> buscarPublicacionesPorTexto(@Param("texto") String texto);
    @Query(value = """
        SELECT p.id, p.titulo, p.latitud, p.longitud,
        (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitud)) *
        cos(radians(p.longitud) - radians(:lng)) +
        sin(radians(:lat)) * sin(radians(p.latitud)))) AS distancia_km
        FROM publicacion p
        WHERE p.estado = 'activa'
        GROUP BY p.id, p.titulo, p.latitud, p.longitud
        HAVING (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitud)) *
        cos(radians(p.longitud) - radians(:lng)) +
        sin(radians(:lat)) * sin(radians(p.latitud)))) <= :radio
        ORDER BY distancia_km ASC
        """, nativeQuery = true)
    List<Object[]> publicacionesCercanas(@Param("lat") Double lat,
                                         @Param("lng") Double lng,
                                         @Param("radio") Double radio);

    @Query(value = """
        SELECT p.id, p.titulo, p.estado, p.direccion_referencia, p.latitud, p.longitud,
               p.fecha_disponibilidad, u.id AS id_usuario, u.nombre AS nombre_usuario,
               u.tipo_cuenta AS tipo_punto, m.nombre AS material, m.categoria,
               pm.cantidad, pm.unidad,
               (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitud)) *
               cos(radians(p.longitud) - radians(:lng)) +
               sin(radians(:lat)) * sin(radians(p.latitud)))) AS distancia_km
        FROM publicacion p
        INNER JOIN usuario u ON p.id_usuario = u.id
        INNER JOIN publicacion_material pm ON p.id = pm.id_publicacion
        INNER JOIN material m ON pm.id_material = m.id
        WHERE p.estado = 'activa'
        AND (:material IS NULL OR m.nombre ILIKE CONCAT('%', :material, '%'))
        AND (:categoria IS NULL OR LOWER(m.categoria) = LOWER(:categoria))
        AND (:tipoPunto IS NULL OR LOWER(u.tipo_cuenta) = LOWER(:tipoPunto))
        AND (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitud)) *
             cos(radians(p.longitud) - radians(:lng)) +
             sin(radians(:lat)) * sin(radians(p.latitud)))) <= :radioKm
        ORDER BY distancia_km ASC, p.created_at DESC
        """, nativeQuery = true)
    List<Object[]> publicacionesMapa(@Param("lat") Double lat,
                                     @Param("lng") Double lng,
                                     @Param("radioKm") Double radioKm,
                                     @Param("material") String material,
                                     @Param("categoria") String categoria,
                                     @Param("tipoPunto") String tipoPunto);

    @Query(value = """
        SELECT p.id, p.titulo, p.latitud, p.longitud, m.nombre, pm.cantidad
        FROM publicacion p
        INNER JOIN publicacion_material pm ON p.id = pm.id_publicacion
        INNER JOIN material m ON pm.id_material = m.id
        WHERE p.estado = 'activa'
        AND LOWER(m.categoria) = LOWER(:categoria)
        ORDER BY p.created_at DESC
        """, nativeQuery = true)
    List<Object[]> publicacionesPorCategoriaMaterial(@Param("categoria") String categoria);

    @Query(value = """
        SELECT p.id, p.titulo, p.estado, p.fecha_disponibilidad, p.direccion_referencia,
               m.nombre, pm.cantidad, pm.unidad,
               r.id AS id_recoleccion, r.estado AS estado_recoleccion, r.fecha_programada,
               u.id AS id_recolector, u.nombre AS nombre_recolector
        FROM publicacion p
        INNER JOIN publicacion_material pm ON p.id = pm.id_publicacion
        INNER JOIN material m ON pm.id_material = m.id
        LEFT JOIN recoleccion r ON r.id_publicacion = p.id
        LEFT JOIN usuario u ON r.id_recolector = u.id
        WHERE p.id_usuario = :idUsuario
        ORDER BY COALESCE(r.fecha_programada, p.created_at) DESC
        """, nativeQuery = true)
    List<Object[]> historialMaterialesUsuario(@Param("idUsuario") UUID idUsuario);
}
