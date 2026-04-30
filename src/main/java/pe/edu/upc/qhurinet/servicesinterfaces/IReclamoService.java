package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Reclamo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IReclamoService {
    public List<Reclamo> list();
    public Reclamo insert(Reclamo r);
    public Optional<Reclamo> listId(UUID id);
    public void update(Reclamo r);
    public void delete(UUID id);
}
