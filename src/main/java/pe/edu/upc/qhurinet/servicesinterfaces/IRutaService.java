package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Ruta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRutaService {
    public List<Ruta> list();
    public Ruta insert(Ruta r);
    public Optional<Ruta> listId(UUID id);
    public void update(Ruta r);
    public void delete(UUID id);
    List<Ruta> listByUsuario(UUID idUsuario);
    List<Ruta> listFavoritasByUsuario(UUID idUsuario);
}
