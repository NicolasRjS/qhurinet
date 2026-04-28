package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Certificado;
import pe.edu.upc.qhurinet.repositories.ICertificadoRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.ICertificadoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificadoServiceImplement implements ICertificadoService {
    @Autowired
    private ICertificadoRepository cR;

    @Override
    public List<Certificado> list() {
        return cR.findAll();
    }

    @Override
    public Certificado insert(Certificado c) {
        return cR.save(c);
    }

    @Override
    public Optional<Certificado> listId(UUID id) {
        return cR.findById(id);
    }

    @Override
    public void update(Certificado c) {
        cR.save(c);
    }

    @Override
    public void delete(UUID id) {
        cR.deleteById(id);
    }
}
