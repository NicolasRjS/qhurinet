package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.Recoleccion;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IRecoleccionRepository extends JpaRepository<Recoleccion, UUID> {
    @Query(value = """
        SELECT r.id, r.fecha_programada, r.estado, p.titulo,
        CASE WHEN p.id_usuario = :idUsuario THEN 'emisor' ELSE 'recolector' END AS rol_usuario
        FROM recoleccion r
        INNER JOIN publicacion p ON r.id_publicacion = p.id
        WHERE (p.id_usuario = :idUsuario OR r.id_recolector = :idUsuario)
        AND (:fechaIni IS NULL OR CAST(r.fecha_programada AS date) >= :fechaIni)
        AND (:fechaFin IS NULL OR CAST(r.fecha_programada AS date) <= :fechaFin)
        AND (:estado IS NULL OR r.estado = :estado)
        ORDER BY r.fecha_programada DESC
        """, nativeQuery = true)
    List<Object[]> historialUsuario(@Param("idUsuario") UUID idUsuario,
                                    @Param("fechaIni") LocalDate fechaIni,
                                    @Param("fechaFin") LocalDate fechaFin,
                                    @Param("estado") String estado);
}
