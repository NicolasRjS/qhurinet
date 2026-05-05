package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.repositories.IUsuarioRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServiceImplement implements IUsuarioService {
    @Autowired
    private IUsuarioRepository uR;

    @Override
    public List<Usuario> list() {
        return uR.findAll();
    }

    @Override
    public Usuario insert(Usuario u) {
        return uR.save(u);
    }

    @Override
    public Optional<Usuario> listId(UUID id) {
        return uR.findById(id);
    }

    @Override
    public void update(Usuario u) {
        uR.save(u);
    }

    @Override
    public void delete(UUID id) {
        uR.deleteById(id);
    }

    @Override
    public List<Object[]> kgRecicladosPorMes(UUID idUsuario) {
        return uR.kgRecicladosPorMes(idUsuario);
    }

    @Override
    public List<Object[]> rankingUsuariosPorPuntos() {
        return uR.rankingUsuariosPorPuntos();
    }
}
