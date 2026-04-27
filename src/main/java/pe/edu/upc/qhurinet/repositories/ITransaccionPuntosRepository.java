package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;

import java.util.UUID;

public interface ITransaccionPuntosRepository extends JpaRepository<TransaccionPuntos, UUID> {
}
