package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.SoporteContacto;
import pe.edu.upc.qhurinet.repositories.ISoporteContactoRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.ISoporteContactoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SoporteContactoServiceImplement implements ISoporteContactoService {
    @Autowired
    private ISoporteContactoRepository sR;

    @Override
    public List<SoporteContacto> list() {
        return sR.findAll();
    }

    @Override
    public SoporteContacto insert(SoporteContacto c) {
        return sR.save(c);
    }

    @Override
    public Optional<SoporteContacto> listId(UUID id) {
        return sR.findById(id);
    }

    @Override
    public void update(SoporteContacto c) {
        sR.save(c);
    }

    @Override
    public void delete(UUID id) {
        sR.deleteById(id);
    }

    @Override
    public List<SoporteContacto> listActivos() {
        return sR.findByActivoTrueOrderByTipoAsc();
    }
}
