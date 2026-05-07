package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Recoleccion;
import pe.edu.upc.qhurinet.repositories.IRecoleccionRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IRecoleccionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecoleccionServiceImplement implements IRecoleccionService {
    @Autowired
    private IRecoleccionRepository rR;

    @Override
    public List<Recoleccion> list() {
        return rR.findAll();
    }

    @Override
    public Recoleccion insert(Recoleccion r) {
        return rR.save(r);
    }

    @Override
    public Optional<Recoleccion> listId(UUID id) {
        return rR.findById(id);
    }

    @Override
    public void update(Recoleccion r) {
        rR.save(r);
    }

    @Override
    public void delete(UUID id) {
        rR.deleteById(id);
    }

    @Override
    public List<Object[]> historialUsuario(UUID idUsuario, LocalDate fechaIni, LocalDate fechaFin, String estado) {
        return rR.historialUsuario(idUsuario, fechaIni, fechaFin, estado);
    }

    @Override
    public List<Object[]> promedioCalificacionRecolector(UUID idRecolector) {
        return rR.promedioCalificacionRecolector(idRecolector);
    }

    @Override
    public List<Object[]> recoleccionesPorRangoYEstado(LocalDate fechaIni, LocalDate fechaFin, String estado) {
        return rR.recoleccionesPorRangoYEstado(fechaIni, fechaFin, estado);
    }

    @Override
    public List<Object[]> disponibilidadPorFecha(LocalDate fecha) {
        return rR.disponibilidadPorFecha(fecha);
    }

    @Override
    public List<Object[]> recoleccionesPendientesRecolector(UUID idRecolector) {
        return rR.recoleccionesPendientesRecolector(idRecolector);
    }

    @Override
    public List<Object[]> incidenciasUsuario(UUID idUsuario) {
        return rR.incidenciasUsuario(idUsuario);
    }

    @Override
    public List<Object[]> actividadesDetalle(UUID idUsuario, LocalDate fechaIni, LocalDate fechaFin, String estado) {
        return rR.actividadesDetalle(idUsuario, fechaIni, fechaFin, estado);
    }
}
