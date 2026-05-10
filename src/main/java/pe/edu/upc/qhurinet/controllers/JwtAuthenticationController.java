package pe.edu.upc.qhurinet.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.qhurinet.dtos.AuthRegisterRequestDTO;
import pe.edu.upc.qhurinet.dtos.JwtRequestDTO;
import pe.edu.upc.qhurinet.dtos.JwtResponseDTO;
import pe.edu.upc.qhurinet.dtos.RefreshTokenRequestDTO;
import pe.edu.upc.qhurinet.dtos.SocialLoginRequestDTO;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.repositories.IUsuarioRepository;
import pe.edu.upc.qhurinet.securities.JwtTokenUtil;
import pe.edu.upc.qhurinet.securities.LoginRateLimiter;
import pe.edu.upc.qhurinet.servicesimplements.JwtUserDetailsService;
import pe.edu.upc.qhurinet.servicesinterfaces.IRefreshTokenService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginRateLimiter loginRateLimiter;

    @PostMapping({"/login", "/auth/login"})
    public ResponseEntity<?> login(@RequestBody JwtRequestDTO req, HttpServletRequest request) throws Exception {
        if (req == null || isBlank(req.getUsername()) || isBlank(req.getPassword())) {
            return ResponseEntity.badRequest().body("Username y password son obligatorios");
        }
        if (!loginRateLimiter.allow(rateLimitKey(req.getUsername(), request))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too Many Requests");
        }

        authenticate(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(issueTokens(req.getUsername()));
    }

    @PostMapping("/auth/registro")
    public ResponseEntity<?> registro(@RequestBody AuthRegisterRequestDTO req) {
        String error = validarRegistro(req);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (usuarioRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username ya registrado");
        }
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(req.getNombre().trim());
        usuario.setEmail(req.getEmail().trim());
        usuario.setUsername(req.getUsername().trim());
        usuario.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        usuario.setTelefono(req.getTelefono());
        usuario.setTipoCuenta(isBlank(req.getTipoCuenta()) ? "GENERADOR" : req.getTipoCuenta().trim());
        usuario.setProveedorAuth("local");
        usuario.setDisponible(true);
        usuario.setVerificado(false);
        usuario.setPuntosTotales(0);
        usuario.setNivelParticipacion("Bronce");
        usuario.syncRoles(req.getRoles() == null || req.getRoles().isEmpty() ? List.of("GENERADOR") : req.getRoles());
        usuarioService.insert(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(issueTokens(usuario.getUsername()));
    }

    @PostMapping("/auth/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequestDTO req) {
        String error = validarSocialLogin(req);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        String email = req.getEmail().trim().toLowerCase(Locale.ROOT);
        Optional<Usuario> existente = usuarioRepository.findByEmail(email);
        if (existente.isPresent()) {
            return ResponseEntity.ok(issueTokens(existente.get().getUsername()));
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(req.getNombre().trim());
        usuario.setEmail(email);
        usuario.setUsername(usernameDisponible(req));
        usuario.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setTelefono(null);
        usuario.setTipoCuenta(isBlank(req.getTipoCuenta()) ? "GENERADOR" : req.getTipoCuenta().trim());
        usuario.setProveedorAuth(req.getProvider().trim().toLowerCase(Locale.ROOT));
        usuario.setDisponible(true);
        usuario.setVerificado(true);
        usuario.setPuntosTotales(0);
        usuario.setNivelParticipacion("Bronce");
        usuario.syncRoles(req.getRoles() == null || req.getRoles().isEmpty() ? List.of("GENERADOR") : req.getRoles());
        usuarioService.insert(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(issueTokens(usuario.getUsername()));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDTO req) {
        if (req == null || isBlank(req.getRefreshToken())) {
            return ResponseEntity.badRequest().body("refresh_token obligatorio");
        }

        var token = refreshTokenService.validate(req.getRefreshToken());
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token invalido");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(token.get().getUsuario().getUsername());
        String accessToken = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponseDTO(
                accessToken,
                req.getRefreshToken(),
                jwtTokenUtil.getAccessTokenValiditySeconds()
        ));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDTO req) {
        if (req == null || isBlank(req.getRefreshToken())) {
            return ResponseEntity.badRequest().body("refresh_token obligatorio");
        }
        refreshTokenService.revoke(req.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    private JwtResponseDTO issueTokens(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Usuario usuario = usuarioRepository.findOneByUsername(username);
        String accessToken = jwtTokenUtil.generateToken(userDetails);
        String refreshToken = refreshTokenService.createToken(usuario);
        return new JwtResponseDTO(accessToken, refreshToken, jwtTokenUtil.getAccessTokenValiditySeconds());
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    private String validarRegistro(AuthRegisterRequestDTO req) {
        if (req == null || isBlank(req.getNombre()) || isBlank(req.getEmail())
                || isBlank(req.getUsername()) || isBlank(req.getPassword())) {
            return "Nombre, email, username y password son obligatorios";
        }
        return null;
    }

    private String validarSocialLogin(SocialLoginRequestDTO req) {
        if (req == null || isBlank(req.getProvider()) || isBlank(req.getEmail()) || isBlank(req.getNombre())) {
            return "Provider, email y nombre son obligatorios";
        }
        String provider = req.getProvider().trim().toLowerCase(Locale.ROOT);
        if (!provider.equals("google") && !provider.equals("facebook")) {
            return "Provider no soportado";
        }
        return null;
    }

    private String usernameDisponible(SocialLoginRequestDTO req) {
        String base = isBlank(req.getUsername()) ? req.getEmail().split("@")[0] : req.getUsername();
        base = base.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "");
        if (base.isBlank()) {
            base = "social";
        }

        String candidato = base;
        int intento = 1;
        while (usuarioRepository.existsByUsername(candidato)) {
            candidato = base + intento;
            intento++;
        }
        return candidato;
    }

    private String rateLimitKey(String username, HttpServletRequest request) {
        return request.getRemoteAddr() + ":" + username.toLowerCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
