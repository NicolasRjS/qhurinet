package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.MensajeChat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IMensajeChatService {
    public List<MensajeChat> list();
    public MensajeChat insert(MensajeChat m);
    public Optional<MensajeChat> listId(UUID id);
    public void update(MensajeChat m);
    public void delete(UUID id);
}
