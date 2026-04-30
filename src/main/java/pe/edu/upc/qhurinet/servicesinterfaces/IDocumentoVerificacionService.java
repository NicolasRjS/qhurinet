package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.DocumentoVerificacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDocumentoVerificacionService {
    public List<DocumentoVerificacion> list();
    public DocumentoVerificacion insert(DocumentoVerificacion d);
    public Optional<DocumentoVerificacion> listId(UUID id);
    public void update(DocumentoVerificacion d);
    public void delete(UUID id);
}
