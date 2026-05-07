package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.UsuarioIncentivo;

import java.util.List;
import java.util.UUID;

public interface IUsuarioIncentivoRepository extends JpaRepository<UsuarioIncentivo, UUID> {
    @Query(value = """
        SELECT ui.id, i.id, i.nombre, i.tipo, i.meta_cantidad, i.meta_unidad,
               ui.cantidad_actual, ui.estado, ui.completado_en,
               CASE WHEN ui.estado = 'completado' AND ui.completado_en IS NULL THEN true ELSE false END AS puede_reclamar
        FROM usuario_incentivo ui
        INNER JOIN incentivo i ON ui.id_incentivo = i.id
        WHERE ui.id_usuario = :idUsuario
        ORDER BY i.tipo ASC, i.nombre ASC
        """, nativeQuery = true)
    List<Object[]> progresoIncentivosUsuario(@Param("idUsuario") UUID idUsuario);
}
