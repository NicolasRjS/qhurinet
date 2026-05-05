package pe.edu.upc.qhurinet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.KgPorMesDTO;
import pe.edu.upc.qhurinet.dtos.UsuarioDTO;
import pe.edu.upc.qhurinet.entities.Role;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private IUsuarioService uS;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/lista")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> lista = uS.list()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        updateEntityFromDto(usuario, dto, true);
        Usuario cur = uS.insert(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(cur));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        Optional<Usuario> usu = uS.listId(id);

        if (usu.isPresent()) {
            return ResponseEntity.ok(toDto(usu.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioDTO dto) {
        Optional<Usuario> existente = uS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        updateEntityFromDto(existente.get(), dto, false);
        uS.update(existente.get());
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Usuario> usuario = uS.listId(id);

        if (usuario.isPresent()) {
            uS.delete(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    private UsuarioDTO toDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setUsername(usuario.getUsername());
        dto.setTelefono(usuario.getTelefono());
        dto.setFotoUrl(usuario.getFotoUrl());
        dto.setDescripcion(usuario.getDescripcion());
        dto.setRoles(usuario.getRoles()
                .stream()
                .map(Role::getRol)
                .collect(Collectors.toList()));
        dto.setTipoCuenta(usuario.getTipoCuenta());
        dto.setProveedorAuth(usuario.getProveedorAuth());
        dto.setDisponible(usuario.getDisponible());
        dto.setVerificado(usuario.getVerificado());
        dto.setPuntosTotales(usuario.getPuntosTotales());
        dto.setNivelParticipacion(usuario.getNivelParticipacion());
        dto.setCreatedAt(usuario.getCreatedAt());
        dto.setUpdatedAt(usuario.getUpdatedAt());
        dto.setPasswordHash(null);
        return dto;
    }

    private void updateEntityFromDto(Usuario usuario, UsuarioDTO dto, boolean isNew) {
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setUsername(dto.getUsername());
        usuario.setTelefono(dto.getTelefono());
        usuario.setFotoUrl(dto.getFotoUrl());
        usuario.setDescripcion(dto.getDescripcion());
        usuario.setTipoCuenta(dto.getTipoCuenta());
        usuario.setProveedorAuth(dto.getProveedorAuth());
        usuario.setDisponible(dto.getDisponible());
        usuario.setVerificado(dto.getVerificado());
        usuario.setPuntosTotales(dto.getPuntosTotales());
        usuario.setNivelParticipacion(dto.getNivelParticipacion());

        if (dto.getPasswordHash() != null && !dto.getPasswordHash().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));
        } else if (isNew) {
            usuario.setPasswordHash(passwordEncoder.encode("temporal123"));
        }

        usuario.syncRoles(normalizeRoles(dto.getRoles()));
    }

    private List<String> normalizeRoles(List<String> roleNames) {
        List<String> normalizedRoles = roleNames;
        if (normalizedRoles == null || normalizedRoles.isEmpty()) {
            normalizedRoles = List.of("GENERADOR");
        }

        List<String> roles = new ArrayList<>();
        for (String roleName : normalizedRoles) {
            if (roleName == null || roleName.isBlank()) {
                continue;
            }
            roles.add(roleName.trim().toUpperCase());
        }
        return roles;
    }

    @GetMapping("/{idUsuario}/estadisticas/kg-por-mes")
    public ResponseEntity<?> kgRecicladosPorMes(@PathVariable UUID idUsuario) {
        List<Object[]> lista = uS.kgRecicladosPorMes(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<KgPorMesDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            KgPorMesDTO dto = new KgPorMesDTO();
            dto.setMes((String) fila[0]);
            dto.setTotalKg(((Number) fila[1]).doubleValue());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }
}
