package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Certificado;

import java.util.UUID;

public interface ICertificadoRepository extends JpaRepository<Certificado, UUID> {
}
