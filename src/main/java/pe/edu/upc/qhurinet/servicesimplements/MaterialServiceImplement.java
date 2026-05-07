package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Material;
import pe.edu.upc.qhurinet.repositories.IMaterialRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IMaterialService;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialServiceImplement implements IMaterialService {
    @Autowired
    private IMaterialRepository mR;

    @Override
    public List<Material> list() {
        return mR.findAll();
    }

    @Override
    public Material insert(Material m) {
        return mR.save(m);
    }

    @Override
    public Optional<Material> listId(int id) {
        return mR.findById(id);
    }

    @Override
    public void update(Material m) {
        mR.save(m);
    }

    @Override
    public void delete(int id) {
        mR.deleteById(id);
    }

    @Override
    public List<Object[]> top5MaterialesMasReciclados() {
        return mR.top5MaterialesMasReciclados();
    }

    @Override
    public List<Object[]> sugerirCategoriaMaterial(String texto) {
        return mR.sugerirCategoriaMaterial(texto);
    }
}
