package pe.edu.upc.qhurinet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.EstadisticasResumenDTO;
import pe.edu.upc.qhurinet.dtos.KgPorMesDTO;
import pe.edu.upc.qhurinet.dtos.PerfilRecolectorDTO;
import pe.edu.upc.qhurinet.dtos.ResenaRecolectorDTO;
import pe.edu.upc.qhurinet.dtos.UsuarioDTO;
import pe.edu.upc.qhurinet.dtos.UsuarioRankingDTO;
import pe.edu.upc.qhurinet.entities.Role;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.time.LocalDateTime;
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

    @GetMapping("/ranking")
    public ResponseEntity<?> rankingUsuarios() {
        List<Object[]> lista = uS.rankingUsuariosPorPuntos();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }
        List<UsuarioRankingDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            UsuarioRankingDTO dto = new UsuarioRankingDTO();
            dto.setIdUsuario((UUID) fila[0]);
            dto.setNombre((String) fila[1]);
            dto.setPuntosTotales(((Number) fila[2]).intValue());
            dto.setNivelParticipacion((String) fila[3]);
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{idRecolector}/perfil-recolector")
    public ResponseEntity<?> perfilRecolector(@PathVariable UUID idRecolector) {
        List<Object[]> lista = uS.perfilRecolector(idRecolector);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Object[] fila = lista.get(0);
        PerfilRecolectorDTO dto = new PerfilRecolectorDTO();
        dto.setIdRecolector(toUuid(fila[0]));
        dto.setNombre((String) fila[1]);
        dto.setEmail((String) fila[2]);
        dto.setTelefono((String) fila[3]);
        dto.setFotoUrl((String) fila[4]);
        dto.setDescripcion((String) fila[5]);
        dto.setDisponible(toBoolean(fila[6]));
        dto.setVerificado(toBoolean(fila[7]));
        dto.setPuntosTotales(toInteger(fila[8]));
        dto.setNivelParticipacion((String) fila[9]);
        dto.setPuntuacionPromedio(toDouble(fila[10]));
        dto.setTotalValoraciones(toLong(fila[11]));
        dto.setTotalRecolecciones(toLong(fila[12]));
        dto.setComentariosDestacados(mapComentariosRecolector(uS.comentariosRecolector(idRecolector)));

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{idUsuario}/estadisticas/resumen")
    public ResponseEntity<?> estadisticasResumen(@PathVariable UUID idUsuario) {
        List<Object[]> lista = uS.estadisticasResumenUsuario(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Object[] fila = lista.get(0);
        EstadisticasResumenDTO dto = new EstadisticasResumenDTO();
        dto.setIdUsuario(toUuid(fila[0]));
        dto.setNombre((String) fila[1]);
        dto.setPuntosTotales(toInteger(fila[2]));
        dto.setNivelParticipacion((String) fila[3]);
        dto.setPublicaciones(toLong(fila[4]));
        dto.setEntregasComoEmisor(toLong(fila[5]));
        dto.setRecojosComoRecolector(toLong(fila[6]));
        dto.setKgReciclados(toDouble(fila[7]));
        dto.setIncidencias(toLong(fila[8]));

        return ResponseEntity.ok(dto);
    }

    private List<ResenaRecolectorDTO> mapComentariosRecolector(List<Object[]> filas) {
        List<ResenaRecolectorDTO> comentarios = new ArrayList<>();
        for (Object[] fila : filas) {
            ResenaRecolectorDTO dto = new ResenaRecolectorDTO();
            dto.setIdCalificacion(toUuid(fila[0]));
            dto.setPuntuacion(toInteger(fila[1]));
            dto.setComentario((String) fila[2]);
            dto.setCreatedAt(toLocalDateTime(fila[3]));
            dto.setAutor((String) fila[4]);
            comentarios.add(dto);
        }
        return comentarios;
    }

    private UUID toUuid(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(value.toString());
    }

    private Integer toInteger(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private Double toDouble(Object value) {
        return value == null ? null : ((Number) value).doubleValue();
    }

    private Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        return LocalDateTime.parse(value.toString().replace(" ", "T"));
    }
}
