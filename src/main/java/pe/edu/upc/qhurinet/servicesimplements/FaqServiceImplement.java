package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Faq;
import pe.edu.upc.qhurinet.repositories.IFaqRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IFaqService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FaqServiceImplement implements IFaqService {
    @Autowired
    private IFaqRepository fR;

    @Override
    public List<Faq> list() {
        return fR.findAll();
    }

    @Override
    public Faq insert(Faq f) {
        return fR.save(f);
    }

    @Override
    public Optional<Faq> listId(UUID id) {
        return fR.findById(id);
    }

    @Override
    public void update(Faq f) {
        fR.save(f);
    }

    @Override
    public void delete(UUID id) {
        fR.deleteById(id);
    }

    @Override
    public List<Faq> listActivas() {
        return fR.findByActivoTrueOrderByCategoriaAscPreguntaAsc();
    }

    @Override
    public List<Faq> buscarActivas(String categoria, String texto) {
        return fR.buscarActivas(categoria, texto);
    }
}
