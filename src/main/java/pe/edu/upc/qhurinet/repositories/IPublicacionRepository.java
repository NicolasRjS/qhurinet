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
}
