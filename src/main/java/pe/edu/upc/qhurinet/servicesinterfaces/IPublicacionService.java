package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Publicacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPublicacionService {
    public List<Publicacion> list();
    public Publicacion insert(Publicacion p);
    public Optional<Publicacion> listId(UUID id);
    public void update(Publicacion p);
    public void delete(UUID id);
    public List<Object[]> buscarPublicacionesPorTexto(String texto);
    List<Object[]> publicacionesCercanas(Double lat, Double lng, Double radio);
    List<Object[]> publicacionesPorCategoriaMaterial(String categoria);

}
