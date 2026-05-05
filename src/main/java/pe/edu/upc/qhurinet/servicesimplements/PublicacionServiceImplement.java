package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Publicacion;
import pe.edu.upc.qhurinet.repositories.IPublicacionRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PublicacionServiceImplement implements IPublicacionService {
    @Autowired
    private IPublicacionRepository pR;

    @Override
    public List<Publicacion> list() {
        return pR.findAll();
    }

    @Override
    public Publicacion insert(Publicacion p) {
        return pR.save(p);
    }

    @Override
    public Optional<Publicacion> listId(UUID id) {
        return pR.findById(id);
    }

    @Override
    public void update(Publicacion p) {
        pR.save(p);
    }

    @Override
    public void delete(UUID id) {
        pR.deleteById(id);
    }

    @Override
    public List<Object[]> buscarPublicacionesPorTexto(String texto) {
        return pR.buscarPublicacionesPorTexto(texto);
    }
    @Override
    public List<Object[]> publicacionesCercanas(Double lat, Double lng, Double radio) {
        return pR.publicacionesCercanas(lat, lng, radio);
    }
    @Override
    public List<Object[]> publicacionesPorCategoriaMaterial(String categoria) {
        return pR.publicacionesPorCategoriaMaterial(categoria);
    }
}
