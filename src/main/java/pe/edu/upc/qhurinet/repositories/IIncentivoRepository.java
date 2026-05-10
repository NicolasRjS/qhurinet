package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.Incentivo;

import java.util.List;
import java.util.UUID;

public interface IIncentivoRepository extends JpaRepository<Incentivo, UUID> {
    @Query(value = """
        SELECT i.id, i.tipo, i.nombre, i.descripcion, i.costo_puntos, i.stock, i.activo,
               u.puntos_totales,
               CASE WHEN u.puntos_totales >= i.costo_puntos THEN true ELSE false END AS puntos_suficientes,
               CASE WHEN ui.id IS NULL THEN false ELSE true END AS ya_registrado
        FROM incentivo i
        CROSS JOIN usuario u
        LEFT JOIN usuario_incentivo ui ON ui.id_incentivo = i.id AND ui.id_usuario = u.id
        WHERE u.id = :idUsuario
        AND i.activo = true
        ORDER BY i.costo_puntos ASC, i.nombre ASC
        """, nativeQuery = true)
    List<Object[]> incentivosDisponiblesUsuario(@Param("idUsuario") UUID idUsuario);
}
