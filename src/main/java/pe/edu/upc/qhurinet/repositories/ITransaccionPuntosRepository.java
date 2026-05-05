package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;

import java.util.List;
import java.util.UUID;

public interface ITransaccionPuntosRepository extends JpaRepository<TransaccionPuntos, UUID> {

    @Query(value = """
            SELECT u.id, u.nombre, COALESCE(SUM(tp.puntos), 0) AS total_puntos_mes
            FROM usuario u
            LEFT JOIN transaccion_puntos tp ON u.id = tp.id_usuario
            AND tp.tipo = 'ganado'
            AND TO_CHAR(tp.created_at, 'YYYY-MM') = :mes
            WHERE u.id = :idUsuario
            GROUP BY u.id, u.nombre
            """, nativeQuery = true)
    List<Object[]> totalPuntosGanadosPorMes(@Param("idUsuario") UUID idUsuario,
                                            @Param("mes") String mes);
}