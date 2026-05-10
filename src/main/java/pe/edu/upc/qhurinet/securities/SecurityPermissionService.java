package pe.edu.upc.qhurinet.securities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.dtos.MetodoPagoDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionDTO;
import pe.edu.upc.qhurinet.dtos.RecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.RutaDTO;
import pe.edu.upc.qhurinet.dtos.UsuarioDTO;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.repositories.IDocumentoVerificacionRepository;
import pe.edu.upc.qhurinet.repositories.IMetodoPagoRepository;
import pe.edu.upc.qhurinet.repositories.INotificacionRepository;
import pe.edu.upc.qhurinet.repositories.IPublicacionRepository;
import pe.edu.upc.qhurinet.repositories.IRecoleccionRepository;
import pe.edu.upc.qhurinet.repositories.IRutaRepository;
import pe.edu.upc.qhurinet.repositories.IUsuarioRepository;

import java.util.Optional;
import java.util.UUID;

@Service("securityPermissionService")
public class SecurityPermissionService {
    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IPublicacionRepository publicacionRepository;

    @Autowired
    private IRecoleccionRepository recoleccionRepository;

    @Autowired
    private IRutaRepository rutaRepository;

    @Autowired
    private IMetodoPagoRepository metodoPagoRepository;

    @Autowired
    private IDocumentoVerificacionRepository documentoRepository;

    @Autowired
    private INotificacionRepository notificacionRepository;

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
    }

    public boolean isSelf(UUID idUsuario) {
        return idUsuario != null && currentUser()
                .map(usuario -> idUsuario.equals(usuario.getId()))
                .orElse(false);
    }

    public boolean isSelf(UsuarioDTO dto) {
        return dto != null && isSelf(dto.getId());
    }

    public boolean canCreateForUser(UUID idUsuario) {
        return isAdmin() || isSelf(idUsuario);
    }

    public boolean canCreatePublicacion(PublicacionDTO dto) {
        return dto != null && canCreateForUser(dto.getIdUsuario());
    }

    public boolean isPublicacionOwner(UUID idPublicacion) {
        if (idPublicacion == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        return currentUser()
                .flatMap(usuario -> publicacionRepository.findById(idPublicacion)
                        .map(publicacion -> publicacion.getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }

    public boolean isPublicacionOwner(PublicacionDTO dto) {
        return dto != null && isPublicacionOwner(dto.getId());
    }

    public boolean canCreateRecoleccion(RecoleccionDTO dto) {
        if (dto == null) {
            return false;
        }
        if (isAdmin() || isSelf(dto.getIdRecolector())) {
            return true;
        }
        return isPublicacionOwner(dto.getIdPublicacion());
    }

    public boolean isRecoleccionParticipant(UUID idRecoleccion) {
        if (idRecoleccion == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        return currentUser()
                .flatMap(usuario -> recoleccionRepository.findById(idRecoleccion)
                        .map(recoleccion -> recoleccion.getRecolector().getId().equals(usuario.getId())
                                || recoleccion.getPublicacion().getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }

    public boolean isRecoleccionParticipant(RecoleccionDTO dto) {
        return dto != null && isRecoleccionParticipant(dto.getId());
    }

    public boolean canCreateRuta(RutaDTO dto) {
        return dto != null && canCreateForUser(dto.getIdUsuario());
    }

    public boolean isRutaOwner(UUID idRuta) {
        if (idRuta == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        return currentUser()
                .flatMap(usuario -> rutaRepository.findById(idRuta)
                        .map(ruta -> ruta.getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }

    public boolean isRutaOwner(RutaDTO dto) {
        return dto != null && isRutaOwner(dto.getId());
    }

    public boolean canCreateMetodoPago(MetodoPagoDTO dto) {
        return dto != null && canCreateForUser(dto.getIdUsuario());
    }

    public boolean isMetodoPagoOwner(UUID idMetodoPago) {
        if (idMetodoPago == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        return currentUser()
                .flatMap(usuario -> metodoPagoRepository.findById(idMetodoPago)
                        .map(metodoPago -> metodoPago.getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }

    public boolean isMetodoPagoOwner(MetodoPagoDTO dto) {
        return dto != null && isMetodoPagoOwner(dto.getId());
    }

    public boolean isDocumentoOwner(UUID idDocumento) {
        if (idDocumento == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        return currentUser()
                .flatMap(usuario -> documentoRepository.findById(idDocumento)
                        .map(documento -> documento.getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }

    public boolean isNotificacionOwner(UUID idNotificacion) {
        if (idNotificacion == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        return currentUser()
                .flatMap(usuario -> notificacionRepository.findById(idNotificacion)
                        .map(notificacion -> notificacion.getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }

    private Optional<Usuario> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(usuarioRepository.findOneByUsername(authentication.getName()));
    }
}
