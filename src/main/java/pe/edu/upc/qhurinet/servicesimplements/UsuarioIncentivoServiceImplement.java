package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.UsuarioIncentivo;
import pe.edu.upc.qhurinet.repositories.IUsuarioIncentivoRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioIncentivoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioIncentivoServiceImplement implements IUsuarioIncentivoService {
    @Autowired
    private IUsuarioIncentivoRepository uR;

    @Override
    public List<UsuarioIncentivo> list() {
        return uR.findAll();
    }

    @Override
    public UsuarioIncentivo insert(UsuarioIncentivo u) {
        return uR.save(u);
    }

    @Override
    public Optional<UsuarioIncentivo> listId(UUID id) {
        return uR.findById(id);
    }

    @Override
    public void update(UsuarioIncentivo u) {
        uR.save(u);
    }

    @Override
    public void delete(UUID id) {
        uR.deleteById(id);
    }

    @Override
    public List<Object[]> progresoIncentivosUsuario(UUID idUsuario) {
        return uR.progresoIncentivosUsuario(idUsuario);
    }

    @Override
    public List<Object[]> recordatoriosIncentivosUsuario(UUID idUsuario) {
        return uR.recordatoriosIncentivosUsuario(idUsuario);
    }
}
