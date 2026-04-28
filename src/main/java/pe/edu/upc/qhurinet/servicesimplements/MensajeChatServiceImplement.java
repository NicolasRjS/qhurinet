package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.MensajeChat;
import pe.edu.upc.qhurinet.repositories.IMensajeChatRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IMensajeChatService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MensajeChatServiceImplement implements IMensajeChatService {
    @Autowired
    private IMensajeChatRepository mR;

    @Override
    public List<MensajeChat> list() {
        return mR.findAll();
    }

    @Override
    public MensajeChat insert(MensajeChat m) {
        return mR.save(m);
    }

    @Override
    public Optional<MensajeChat> listId(UUID id) {
        return mR.findById(id);
    }

    @Override
    public void update(MensajeChat m) {
        mR.save(m);
    }

    @Override
    public void delete(UUID id) {
        mR.deleteById(id);
    }
}
