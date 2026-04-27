package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.TransaccionPuntos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITransaccionPuntosService {
    public List<TransaccionPuntos> list();
    public TransaccionPuntos insert(TransaccionPuntos t);
    public Optional<TransaccionPuntos> listId(UUID id);
    public void update(TransaccionPuntos t);
    public void delete(UUID id);
}
