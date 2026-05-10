package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.PublicacionMaterialId;
import pe.edu.upc.qhurinet.repositories.IPublicacionMaterialRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionMaterialService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PublicacionMaterialServiceImplement implements IPublicacionMaterialService {
    @Autowired
    private IPublicacionMaterialRepository pR;

    @Override
    public List<PublicacionMaterial> list() {
        return pR.findAll();
    }

    @Override
    public PublicacionMaterial insert(PublicacionMaterial p) {
        return pR.save(p);
    }

    @Override
    public Optional<PublicacionMaterial> listId(PublicacionMaterialId id) {
        return pR.findById(id);
    }

    @Override
    public void update(PublicacionMaterial p) {
        pR.save(p);
    }

    @Override
    public void delete(PublicacionMaterialId id) {
        pR.deleteById(id);
    }

    @Override
    public List<PublicacionMaterial> listByPublicacion(UUID idPublicacion) {
        return pR.findByPublicacionId(idPublicacion);
    }

    @Override
    public BigDecimal puntosPorPublicacion(UUID idPublicacion) {
        return pR.puntosPorPublicacion(idPublicacion);
    }
}
