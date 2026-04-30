package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Certificado;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICertificadoService {
    public List<Certificado> list();
    public Certificado insert(Certificado c);
    public Optional<Certificado> listId(UUID id);
    public void update(Certificado c);
    public void delete(UUID id);
}
