package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ITransaccionPuntosRepository extends JpaRepository<TransaccionPuntos, UUID> {
    List<TransaccionPuntos> findByUsuario_IdAndReferenciaTipoOrderByCreatedAtDesc(UUID idUsuario,
                                                                                  String referenciaTipo);

    boolean existsByUsuario_IdAndReferenciaTipoAndCreatedAtBetween(UUID idUsuario,
                                                                   String referenciaTipo,
                                                                   LocalDateTime inicio,
                                                                   LocalDateTime fin);

    boolean existsByUsuario_IdAndReferenciaTipoAndMotivo(UUID idUsuario,
                                                         String referenciaTipo,
                                                         String motivo);

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

    @Query(value = """
        SELECT tp.id, tp.created_at, tp.tipo, tp.puntos, tp.motivo,
               tp.referencia_tipo, tp.referencia_id,
               SUM(CASE
                   WHEN LOWER(tp.tipo) IN ('ganado', 'bonus', 'acreditado') THEN tp.puntos
                   ELSE -tp.puntos
               END) OVER (ORDER BY tp.created_at ASC, tp.id ASC) AS saldo_acumulado
        FROM transaccion_puntos tp
        WHERE tp.id_usuario = :idUsuario
        ORDER BY tp.created_at DESC, tp.id DESC
        """, nativeQuery = true)
    List<Object[]> historialPuntosConSaldo(@Param("idUsuario") UUID idUsuario);
}
