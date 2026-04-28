package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Reclamo;
import pe.edu.upc.qhurinet.repositories.IReclamoRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IReclamoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReclamoServiceImplement implements IReclamoService {
    @Autowired
    private IReclamoRepository rR;

    @Override
    public List<Reclamo> list() {
        return rR.findAll();
    }

    @Override
    public Reclamo insert(Reclamo r) {
        return rR.save(r);
    }

    @Override
    public Optional<Reclamo> listId(UUID id) {
        return rR.findById(id);
    }

    @Override
    public void update(Reclamo r) {
        rR.save(r);
    }

    @Override
    public void delete(UUID id) {
        rR.deleteById(id);
    }
}
