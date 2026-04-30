package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Calificacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICalificacionService {
    public List<Calificacion> list();
    public Calificacion insert(Calificacion c);
    public Optional<Calificacion> listId(UUID id);
    public void update(Calificacion c);
    public void delete(UUID id);
}
