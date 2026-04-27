package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.MensajeChat;

import java.util.UUID;

public interface IMensajeChatRepository extends JpaRepository<MensajeChat, UUID> {
}
