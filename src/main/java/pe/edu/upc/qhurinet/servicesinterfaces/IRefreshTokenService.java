package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.RefreshToken;
import pe.edu.upc.qhurinet.entities.Usuario;

import java.util.Optional;

public interface IRefreshTokenService {
    String createToken(Usuario usuario);
    Optional<RefreshToken> validate(String rawToken);
    void revoke(String rawToken);
}
