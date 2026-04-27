package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.UsuarioIncentivo;

import java.util.UUID;

public interface IUsuarioIncentivoRepository extends JpaRepository<UsuarioIncentivo, UUID> {
}
