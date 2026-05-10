package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.PublicacionMaterialId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPublicacionMaterialService {
    public List<PublicacionMaterial> list();
    public PublicacionMaterial insert(PublicacionMaterial p);
    public Optional<PublicacionMaterial> listId(PublicacionMaterialId id);
    public void update(PublicacionMaterial p);
    public void delete(PublicacionMaterialId id);
    List<PublicacionMaterial> listByPublicacion(UUID idPublicacion);
    BigDecimal puntosPorPublicacion(UUID idPublicacion);
}
