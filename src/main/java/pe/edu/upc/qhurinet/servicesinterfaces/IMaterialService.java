package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Material;

import java.util.List;
import java.util.Optional;

public interface IMaterialService {
    public List<Material> list();
    public Material insert(Material m);
    public Optional<Material> listId(int id);
    public void update(Material m);
    public void delete(int id);
}
