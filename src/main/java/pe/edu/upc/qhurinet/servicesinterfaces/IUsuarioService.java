package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUsuarioService {
    public List<Usuario> list();
    public Usuario insert(Usuario u);
    public Optional<Usuario> listId(UUID id);
    public void update(Usuario u);
    public void delete(UUID id);
    List<Object[]> kgRecicladosPorMes(UUID idUsuario);
    List<Object[]> rankingUsuariosPorPuntos();
}
