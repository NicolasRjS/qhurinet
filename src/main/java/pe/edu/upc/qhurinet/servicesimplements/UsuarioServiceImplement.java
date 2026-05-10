package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.repositories.IUsuarioRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.time.LocalDate;
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
    public List<Object[]> materialesMesActual(UUID idUsuario) {
        return uR.materialesMesActual(idUsuario);
    }

    @Override
    public List<Object[]> rankingUsuariosPorPuntos() {
        return uR.rankingUsuariosPorPuntos();
    }

    @Override
    public List<Object[]> perfilRecolector(UUID idRecolector) {
        return uR.perfilRecolector(idRecolector);
    }

    @Override
    public List<Object[]> comentariosRecolector(UUID idRecolector) {
        return uR.comentariosRecolector(idRecolector);
    }

    @Override
    public List<Object[]> estadisticasResumenUsuario(UUID idUsuario) {
        return uR.estadisticasResumenUsuario(idUsuario);
    }

    @Override
    public List<Object[]> estadisticasResumenUsuarioDesde(UUID idUsuario, LocalDate fechaDesde) {
        return uR.estadisticasResumenUsuarioDesde(idUsuario, fechaDesde);
    }
}
