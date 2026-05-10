package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.SoporteContacto;

import java.util.List;
import java.util.UUID;

public interface ISoporteContactoRepository extends JpaRepository<SoporteContacto, UUID> {
    List<SoporteContacto> findByActivoTrueOrderByTipoAsc();
}
