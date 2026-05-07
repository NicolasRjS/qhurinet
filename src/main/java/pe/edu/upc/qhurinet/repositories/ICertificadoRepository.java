package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.Certificado;

import java.util.List;
import java.util.UUID;

public interface ICertificadoRepository extends JpaRepository<Certificado, UUID> {
    @Query(value = """
        SELECT *
        FROM certificado
        WHERE LOWER(nivel_dificultad) = LOWER(:nivel)
        ORDER BY puntos_requeridos ASC, nombre ASC
        """, nativeQuery = true)
    List<Certificado> certificadosPorDificultad(@Param("nivel") String nivel);
}
