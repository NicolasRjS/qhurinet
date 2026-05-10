package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.Faq;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFaqService {
    List<Faq> list();
    Faq insert(Faq f);
    Optional<Faq> listId(UUID id);
    void update(Faq f);
    void delete(UUID id);
    List<Faq> listActivas();
    List<Faq> buscarActivas(String categoria, String texto);
}
