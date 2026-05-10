package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Ruta;
import pe.edu.upc.qhurinet.repositories.IRutaRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IRutaService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RutaServiceImplement implements IRutaService {
    @Autowired
    private IRutaRepository rR;

    @Override
    public List<Ruta> list() {
        return rR.findAll();
    }

    @Override
    public Ruta insert(Ruta r) {
        return rR.save(r);
    }

    @Override
    public Optional<Ruta> listId(UUID id) {
        return rR.findById(id);
    }

    @Override
    public void update(Ruta r) {
        rR.save(r);
    }

    @Override
    public void delete(UUID id) {
        rR.deleteById(id);
    }

    @Override
    public List<Ruta> listByUsuario(UUID idUsuario) {
        return rR.findByUsuarioIdOrderByCreatedAtDesc(idUsuario);
    }

    @Override
    public List<Ruta> listFavoritasByUsuario(UUID idUsuario) {
        return rR.findByUsuarioIdAndFavoritaTrueOrderByCreatedAtDesc(idUsuario);
    }
}
