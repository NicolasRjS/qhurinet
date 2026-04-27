package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.Usuario;

import java.util.UUID;

public interface IUsuarioRepository extends JpaRepository<Usuario, UUID> {
}
