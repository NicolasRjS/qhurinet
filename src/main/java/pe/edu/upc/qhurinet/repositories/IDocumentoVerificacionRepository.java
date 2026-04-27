package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.DocumentoVerificacion;

import java.util.UUID;

public interface IDocumentoVerificacionRepository extends JpaRepository<DocumentoVerificacion, UUID> {
}
