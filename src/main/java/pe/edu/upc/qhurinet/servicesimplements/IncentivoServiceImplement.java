package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Incentivo;
import pe.edu.upc.qhurinet.repositories.IIncentivoRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IIncentivoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IncentivoServiceImplement implements IIncentivoService {
    @Autowired
    private IIncentivoRepository iR;

    @Override
    public List<Incentivo> list() {
        return iR.findAll();
    }

    @Override
    public Incentivo insert(Incentivo i) {
        return iR.save(i);
    }

    @Override
    public Optional<Incentivo> listId(UUID id) {
        return iR.findById(id);
    }

    @Override
    public void update(Incentivo i) {
        iR.save(i);
    }

    @Override
    public void delete(UUID id) {
        iR.deleteById(id);
    }
}
