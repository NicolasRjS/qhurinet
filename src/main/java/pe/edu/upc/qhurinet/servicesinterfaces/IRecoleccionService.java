package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Recoleccion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRecoleccionService {
    public List<Recoleccion> list();
    public Recoleccion insert(Recoleccion r);
    public Optional<Recoleccion> listId(UUID id);
    public void update(Recoleccion r);
    public void delete(UUID id);
}
