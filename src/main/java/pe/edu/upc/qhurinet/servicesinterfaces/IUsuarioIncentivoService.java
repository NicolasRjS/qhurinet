package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.UsuarioIncentivo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUsuarioIncentivoService {
    public List<UsuarioIncentivo> list();
    public UsuarioIncentivo insert(UsuarioIncentivo u);
    public Optional<UsuarioIncentivo> listId(UUID id);
    public void update(UsuarioIncentivo u);
    public void delete(UUID id);
    List<Object[]> progresoIncentivosUsuario(UUID idUsuario);
    List<Object[]> recordatoriosIncentivosUsuario(UUID idUsuario);
}
