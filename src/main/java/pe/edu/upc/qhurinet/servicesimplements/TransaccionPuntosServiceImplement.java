package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;
import pe.edu.upc.qhurinet.repositories.ITransaccionPuntosRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionPuntosService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransaccionPuntosServiceImplement implements ITransaccionPuntosService {
    @Autowired
    private ITransaccionPuntosRepository tR;

    @Override
    public List<TransaccionPuntos> list() {
        return tR.findAll();
    }

    @Override
    public TransaccionPuntos insert(TransaccionPuntos t) {
        return tR.save(t);
    }

    @Override
    public Optional<TransaccionPuntos> listId(UUID id) {
        return tR.findById(id);
    }

    @Override
    public void update(TransaccionPuntos t) {
        tR.save(t);
    }

    @Override
    public void delete(UUID id) {
        tR.deleteById(id);
    }
    @Override
    public List<Object[]> totalPuntosGanadosPorMes(UUID idUsuario, String mes) {
        return tR.totalPuntosGanadosPorMes(idUsuario, mes);
    }
}
