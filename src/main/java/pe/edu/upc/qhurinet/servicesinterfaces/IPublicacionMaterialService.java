package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.PublicacionMaterialId;

import java.util.List;
import java.util.Optional;

public interface IPublicacionMaterialService {
    public List<PublicacionMaterial> list();
    public PublicacionMaterial insert(PublicacionMaterial p);
    public Optional<PublicacionMaterial> listId(PublicacionMaterialId id);
    public void update(PublicacionMaterial p);
    public void delete(PublicacionMaterialId id);
}
