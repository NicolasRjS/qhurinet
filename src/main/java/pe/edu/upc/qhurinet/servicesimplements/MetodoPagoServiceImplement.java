package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.MetodoPago;
import pe.edu.upc.qhurinet.repositories.IMetodoPagoRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IMetodoPagoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MetodoPagoServiceImplement implements IMetodoPagoService {
    @Autowired
    private IMetodoPagoRepository mR;

    @Override
    public List<MetodoPago> list() {
        return mR.findAll();
    }

    @Override
    public MetodoPago insert(MetodoPago m) {
        return mR.save(m);
    }

    @Override
    public Optional<MetodoPago> listId(UUID id) {
        return mR.findById(id);
    }

    @Override
    public void update(MetodoPago m) {
        mR.save(m);
    }

    @Override
    public void delete(UUID id) {
        mR.deleteById(id);
    }

    @Override
    public List<MetodoPago> listByUsuario(UUID idUsuario) {
        return mR.findByUsuarioIdOrderByCreatedAtDesc(idUsuario);
    }

    @Override
    public List<MetodoPago> listActivosByUsuario(UUID idUsuario) {
        return mR.findByUsuarioIdAndActivoTrueOrderByCreatedAtDesc(idUsuario);
    }
}
