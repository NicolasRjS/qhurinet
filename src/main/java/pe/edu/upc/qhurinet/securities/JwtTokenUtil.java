package pe.edu.upc.qhurinet.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {
    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000;

    @Value("${jwt.secret}")
    private String secret;

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Date getExpirationDate(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(getAllClaims(token));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "roles",
                userDetails.getAuthorities()
                        .stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.joining(","))
        );

        return createToken(claims, userDetails.getUsername());
    }

    public long getAccessTokenValiditySeconds() {
        return ACCESS_TOKEN_VALIDITY / 1000;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return getUsernameFromToken(token).equals(userDetails.getUsername()) && !isExpired(token);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String createToken(Map<String, Object> claims, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private boolean isExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    private SecretKey getSigningKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS512.getJcaName());
    }
}
