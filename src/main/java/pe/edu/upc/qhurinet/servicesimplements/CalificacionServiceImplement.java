package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Calificacion;
import pe.edu.upc.qhurinet.repositories.ICalificacionRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.ICalificacionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CalificacionServiceImplement implements ICalificacionService {
    @Autowired
    private ICalificacionRepository cR;

    @Override
    public List<Calificacion> list() {
        return cR.findAll();
    }

    @Override
    public Calificacion insert(Calificacion c) {
        return cR.save(c);
    }

    @Override
    public Optional<Calificacion> listId(UUID id) {
        return cR.findById(id);
    }

    @Override
    public void update(Calificacion c) {
        cR.save(c);
    }

    @Override
    public void delete(UUID id) {
        cR.deleteById(id);
    }
}
