package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Incentivo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IIncentivoService {
    public List<Incentivo> list();
    public Incentivo insert(Incentivo i);
    public Optional<Incentivo> listId(UUID id);
    public void update(Incentivo i);
    public void delete(UUID id);
    List<Object[]> incentivosDisponiblesUsuario(UUID idUsuario);
}
