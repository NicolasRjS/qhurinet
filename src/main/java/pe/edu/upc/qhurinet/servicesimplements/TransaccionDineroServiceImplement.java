package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.TransaccionDinero;
import pe.edu.upc.qhurinet.repositories.ITransaccionDineroRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionDineroService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransaccionDineroServiceImplement implements ITransaccionDineroService {
    @Autowired
    private ITransaccionDineroRepository tR;

    @Override
    public List<TransaccionDinero> list() {
        return tR.findAll();
    }

    @Override
    public TransaccionDinero insert(TransaccionDinero t) {
        return tR.save(t);
    }

    @Override
    public Optional<TransaccionDinero> listId(UUID id) {
        return tR.findById(id);
    }

    @Override
    public void update(TransaccionDinero t) {
        tR.save(t);
    }

    @Override
    public void delete(UUID id) {
        tR.deleteById(id);
    }
}
