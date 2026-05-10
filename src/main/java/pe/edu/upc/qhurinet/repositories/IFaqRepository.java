package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.qhurinet.entities.Faq;

import java.util.List;
import java.util.UUID;

public interface IFaqRepository extends JpaRepository<Faq, UUID> {
    List<Faq> findByActivoTrueOrderByCategoriaAscPreguntaAsc();

    @Query(value = """
        SELECT *
        FROM faq f
        WHERE f.activo = true
        AND (:categoria IS NULL OR LOWER(f.categoria) = LOWER(:categoria))
        AND (:texto IS NULL OR f.pregunta ILIKE CONCAT('%', :texto, '%')
             OR f.respuesta ILIKE CONCAT('%', :texto, '%'))
        ORDER BY f.categoria ASC, f.pregunta ASC
        """, nativeQuery = true)
    List<Faq> buscarActivas(@Param("categoria") String categoria,
                            @Param("texto") String texto);
}
