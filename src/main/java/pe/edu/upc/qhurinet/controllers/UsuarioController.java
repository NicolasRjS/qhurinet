package pe.edu.upc.qhurinet.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upc.qhurinet.dtos.ArchivoUrlDTO;
import pe.edu.upc.qhurinet.dtos.EstadisticasResumenDTO;
import pe.edu.upc.qhurinet.dtos.HistorialPuntosSaldoDTO;
import pe.edu.upc.qhurinet.dtos.KgPorMesDTO;
import pe.edu.upc.qhurinet.dtos.PerfilRecolectorDTO;
import pe.edu.upc.qhurinet.dtos.PuntosUsuarioDTO;
import pe.edu.upc.qhurinet.dtos.ResenaRecolectorDTO;
import pe.edu.upc.qhurinet.dtos.UsuarioDTO;
import pe.edu.upc.qhurinet.dtos.UsuarioRankingDTO;
import pe.edu.upc.qhurinet.entities.Role;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesimplements.NivelParticipacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IArchivoStorageService;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionPuntosService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ITransaccionPuntosService transaccionPuntosService;

    @Autowired
    private IArchivoStorageService archivoStorageService;

    @Autowired
    private NivelParticipacionService nivelParticipacionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("@securityPermissionService.canCreateForUser(#id)")
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
    @PreAuthorize("@securityPermissionService.canCreateForUser(#dto.id)")
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

    @PatchMapping("/{id}/foto-url")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#id)")
    public ResponseEntity<?> actualizarFotoUrl(@PathVariable UUID id,
                                               @RequestBody ArchivoUrlDTO dto) {
        Optional<Usuario> existente = uS.listId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        if (dto == null || dto.getUrl() == null || dto.getUrl().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("URL de foto obligatoria");
        }

        Usuario usuario = existente.get();
        usuario.setFotoUrl(dto.getUrl());
        uS.update(usuario);
        return ResponseEntity.ok(toDto(usuario));
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@securityPermissionService.canCreateForUser(#id)")
    public ResponseEntity<?> subirFoto(@PathVariable UUID id,
                                       @RequestParam("file") MultipartFile file) {
        Optional<Usuario> existente = uS.listId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        try {
            var archivo = archivoStorageService.guardarImagen(file, "usuarios");
            Usuario usuario = existente.get();
            usuario.setFotoUrl(archivo.getUrl());
            uS.update(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(archivo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar el archivo");
        }
    }

    @DeleteMapping("/{id}/foto")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#id)")
    public ResponseEntity<?> eliminarFoto(@PathVariable UUID id) {
        Optional<Usuario> existente = uS.listId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = existente.get();
        usuario.setFotoUrl(null);
        uS.update(usuario);
        return ResponseEntity.ok(toDto(usuario));
    }

    @PostMapping(value = "/{id}/descripcion-imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@securityPermissionService.canCreateForUser(#id)")
    public ResponseEntity<?> subirImagenDescripcion(@PathVariable UUID id,
                                                    @RequestParam("file") MultipartFile file) {
        Optional<Usuario> existente = uS.listId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        try {
            var archivo = archivoStorageService.guardarImagen(file, "usuarios-descripcion");
            Usuario usuario = existente.get();
            agregarImagenDescripcion(usuario, archivo.getUrl());
            uS.update(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar el archivo");
        }
    }

    @PatchMapping("/{id}/descripcion-imagenes")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#id)")
    public ResponseEntity<?> agregarImagenDescripcionUrl(@PathVariable UUID id,
                                                         @RequestBody ArchivoUrlDTO dto) {
        Optional<Usuario> existente = uS.listId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        if (dto == null || dto.getUrl() == null || dto.getUrl().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("URL de imagen obligatoria");
        }

        Usuario usuario = existente.get();
        agregarImagenDescripcion(usuario, dto.getUrl().trim());
        uS.update(usuario);
        return ResponseEntity.ok(toDto(usuario));
    }

    @DeleteMapping("/{id}/descripcion-imagenes")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#id)")
    public ResponseEntity<?> eliminarImagenesDescripcion(@PathVariable UUID id) {
        Optional<Usuario> existente = uS.listId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = existente.get();
        usuario.setDescripcionImagenesJson("[]");
        uS.update(usuario);
        return ResponseEntity.ok(toDto(usuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
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
        dto.setDescripcionImagenesJson(usuario.getDescripcionImagenesJson());
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
        usuario.setDescripcionImagenesJson(dto.getDescripcionImagenesJson());
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
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
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

    @GetMapping("/{idUsuario}/estadisticas/materiales-mes-actual")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> materialesMesActual(@PathVariable UUID idUsuario) {
        List<Object[]> lista = uS.materialesMesActual(idUsuario);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("material", fila[0]);
            item.put("categoria", fila[1]);
            item.put("cantidad", toDouble(fila[2]));
            item.put("unidad", fila[3]);
            respuesta.add(item);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{idUsuario}/puntos")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> puntosUsuario(@PathVariable UUID idUsuario) {
        Optional<Usuario> usuario = uS.listId(idUsuario);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        PuntosUsuarioDTO dto = new PuntosUsuarioDTO();
        dto.setIdUsuario(idUsuario);
        dto.setSaldo(usuario.get().getPuntosTotales() == null ? 0 : usuario.get().getPuntosTotales());
        dto.setHistorial(mapHistorialPuntos(transaccionPuntosService.historialPuntosConSaldo(idUsuario)));
        return ResponseEntity.ok(dto);
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
            dto.setIdUsuario(toUuid(fila[0]));
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
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> estadisticasResumen(@PathVariable UUID idUsuario) {
        List<Object[]> lista = uS.estadisticasResumenUsuario(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        EstadisticasResumenDTO dto = mapEstadisticasResumen(lista.get(0));

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{idUsuario}/estadisticas")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> estadisticas(@PathVariable UUID idUsuario,
                                          @RequestParam(value = "periodo_meses", required = false) Integer periodoMeses) {
        if (periodoMeses != null && periodoMeses <= 0) {
            return ResponseEntity.badRequest().body("periodo_meses debe ser mayor a cero");
        }

        LocalDate fechaDesde = periodoMeses == null ? null : LocalDate.now().minusMonths(periodoMeses);
        List<Object[]> lista = periodoMeses == null
                ? uS.estadisticasResumenUsuario(idUsuario)
                : uS.estadisticasResumenUsuarioDesde(idUsuario, fechaDesde);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        return ResponseEntity.ok(mapEstadisticasResumen(lista.get(0)));
    }

    @PostMapping("/{idUsuario}/niveles/recalcular")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> recalcularNivelParticipacion(@PathVariable UUID idUsuario) {
        Optional<Usuario> usuario = uS.listId(idUsuario);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        boolean ascendido = nivelParticipacionService.actualizarNivelSiCorresponde(usuario.get());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ascendido", ascendido);
        response.put("usuario", toDto(usuario.get()));
        return ResponseEntity.ok(response);
    }

    private EstadisticasResumenDTO mapEstadisticasResumen(Object[] fila) {
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
        return dto;
    }

    private List<HistorialPuntosSaldoDTO> mapHistorialPuntos(List<Object[]> filas) {
        List<HistorialPuntosSaldoDTO> historial = new ArrayList<>();
        for (Object[] fila : filas) {
            HistorialPuntosSaldoDTO dto = new HistorialPuntosSaldoDTO();
            dto.setIdTransaccion(toUuid(fila[0]));
            dto.setCreatedAt(toLocalDateTime(fila[1]));
            dto.setTipo((String) fila[2]);
            dto.setPuntos(toInteger(fila[3]));
            dto.setMotivo((String) fila[4]);
            dto.setReferenciaTipo((String) fila[5]);
            dto.setReferenciaId(toUuid(fila[6]));
            dto.setSaldoAcumulado(toInteger(fila[7]));
            historial.add(dto);
        }
        return historial;
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
        if (value instanceof byte[] bytes && bytes.length == 16) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            return new UUID(buffer.getLong(), buffer.getLong());
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

    private void agregarImagenDescripcion(Usuario usuario, String url) {
        List<String> imagenes = descripcionImagenes(usuario);
        imagenes.add(url);
        try {
            usuario.setDescripcionImagenesJson(objectMapper.writeValueAsString(imagenes));
        } catch (Exception e) {
            usuario.setDescripcionImagenesJson("[\"" + url.replace("\"", "\\\"") + "\"]");
        }
    }

    private List<String> descripcionImagenes(Usuario usuario) {
        if (usuario.getDescripcionImagenesJson() == null || usuario.getDescripcionImagenesJson().isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(usuario.getDescripcionImagenesJson(), new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
