package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.RefreshToken;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.repositories.IRefreshTokenRepository;
import pe.edu.upc.qhurinet.servicesinterfaces.IRefreshTokenService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenServiceImplement implements IRefreshTokenService {
    private static final int TOKEN_BYTES = 48;
    private static final int TOKEN_DAYS = 7;

    @Autowired
    private IRefreshTokenRepository rR;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String createToken(Usuario usuario) {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setTokenHash(hash(rawToken));
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(TOKEN_DAYS));
        refreshToken.setRevoked(false);
        rR.save(refreshToken);

        return rawToken;
    }

    @Override
    public Optional<RefreshToken> validate(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }

        Optional<RefreshToken> token = rR.findByTokenHash(hash(rawToken));
        if (token.isEmpty()) {
            return Optional.empty();
        }

        RefreshToken refreshToken = token.get();
        if (Boolean.TRUE.equals(refreshToken.getRevoked()) || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        return token;
    }

    @Override
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        rR.findByTokenHash(hash(rawToken)).ifPresent(token -> {
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            rR.save(token);
        });
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar hash de refresh token", e);
        }
    }
}
