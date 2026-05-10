package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.qhurinet.entities.UbicacionRecolectorHistorial;
import pe.edu.upc.qhurinet.repositories.IUbicacionRecolectorHistorialRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IUbicacionRecolectorHistorialService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UbicacionRecolectorHistorialServiceImplement implements IUbicacionRecolectorHistorialService {
    @Autowired
    private IUbicacionRecolectorHistorialRepository uR;

    @Override
    public List<UbicacionRecolectorHistorial> list() {
        return uR.findAll();
    }

    @Override
    public UbicacionRecolectorHistorial insert(UbicacionRecolectorHistorial u) {
        return uR.save(u);
    }

    @Override
    public Optional<UbicacionRecolectorHistorial> listId(UUID id) {
        return uR.findById(id);
    }

    @Override
    public void update(UbicacionRecolectorHistorial u) {
        uR.save(u);
    }

    @Override
    public void delete(UUID id) {
        uR.deleteById(id);
    }

    @Override
    public List<UbicacionRecolectorHistorial> listByRecoleccion(UUID idRecoleccion) {
        return uR.findByRecoleccionIdOrderByCreatedAtDesc(idRecoleccion);
    }

    @Override
    @Transactional
    public void deleteByRecoleccion(UUID idRecoleccion) {
        uR.deleteByRecoleccionId(idRecoleccion);
    }
}
