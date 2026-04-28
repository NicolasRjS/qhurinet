package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.DocumentoVerificacion;
import pe.edu.upc.qhurinet.repositories.IDocumentoVerificacionRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IDocumentoVerificacionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentoVerificacionServiceImplement implements IDocumentoVerificacionService {
    @Autowired
    private IDocumentoVerificacionRepository dR;

    @Override
    public List<DocumentoVerificacion> list() {
        return dR.findAll();
    }

    @Override
    public DocumentoVerificacion insert(DocumentoVerificacion d) {
        return dR.save(d);
    }

    @Override
    public Optional<DocumentoVerificacion> listId(UUID id) {
        return dR.findById(id);
    }

    @Override
    public void update(DocumentoVerificacion d) {
        dR.save(d);
    }

    @Override
    public void delete(UUID id) {
        dR.deleteById(id);
    }
}
