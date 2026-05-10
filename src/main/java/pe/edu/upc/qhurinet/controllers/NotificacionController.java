package pe.edu.upc.qhurinet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.qhurinet.dtos.NotificacionDTO;
import pe.edu.upc.qhurinet.dtos.PushNotificacionRequestDTO;
import pe.edu.upc.qhurinet.entities.Notificacion;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.INotificacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    @Autowired
    private INotificacionService nS;

    @Autowired
    private IUsuarioService uS;

    @GetMapping("/lista")
    public ResponseEntity<List<NotificacionDTO>> listar() {
        List<NotificacionDTO> lista = nS.list()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> listarPorUsuario(@PathVariable UUID idUsuario,
                                              @RequestParam(value = "soloNoLeidas", required = false) Boolean soloNoLeidas) {
        List<NotificacionDTO> lista = Boolean.TRUE.equals(soloNoLeidas)
                ? nS.listNoLeidasByUsuario(idUsuario).stream().map(this::toDto).collect(Collectors.toList())
                : nS.listByUsuario(idUsuario).stream().map(this::toDto).collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/enviar")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#dto.idUsuario)")
    public ResponseEntity<?> enviar(@RequestBody NotificacionDTO dto) {
        if (dto == null || dto.getIdUsuario() == null || isBlank(dto.getTipo())
                || isBlank(dto.getTitulo()) || isBlank(dto.getMensaje())) {
            return ResponseEntity.badRequest().body("Usuario, tipo, titulo y mensaje son obligatorios");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Notificacion n = new Notificacion();
        n.setUsuario(usuario.get());
        n.setTipo(dto.getTipo());
        n.setTitulo(dto.getTitulo());
        n.setMensaje(dto.getMensaje());
        n.setLeida(Boolean.FALSE);
        n.setEstado(isBlank(dto.getEstado()) ? "pendiente" : dto.getEstado());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toDto(nS.insert(n)));
    }

    @PostMapping("/push")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#dto.idUsuario)")
    public ResponseEntity<?> enviarPushMock(@RequestBody PushNotificacionRequestDTO dto) {
        if (dto == null || dto.getIdUsuario() == null || isBlank(dto.getDeviceToken())
                || isBlank(dto.getTitulo()) || isBlank(dto.getMensaje())) {
            return ResponseEntity.badRequest().body("Usuario, deviceToken, titulo y mensaje son obligatorios");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Notificacion n = new Notificacion();
        n.setUsuario(usuario.get());
        n.setTipo(isBlank(dto.getTipo()) ? "push" : dto.getTipo());
        n.setTitulo(dto.getTitulo());
        n.setMensaje(dto.getMensaje());
        n.setLeida(false);
        n.setEstado("enviada");
        n.setErrorMensaje("push mock enviado a token " + maskToken(dto.getDeviceToken()));
        n.setEnviadaAt(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toDto(nS.insert(n)));
    }

    @GetMapping("/cola")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> listarCola(@RequestParam(value = "estado", defaultValue = "pendiente") String estado) {
        List<NotificacionDTO> lista = nS.listByEstado(estado)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/generar-logros/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> generarLogros(@PathVariable UUID idUsuario) {
        Optional<Usuario> usuario = uS.listId(idUsuario);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        List<NotificacionDTO> generadas = new ArrayList<>();
        Usuario u = usuario.get();
        int puntos = u.getPuntosTotales() == null ? 0 : u.getPuntosTotales();

        crearLogroSiNoExiste(generadas, u, puntos >= 100, "Logro: 100 puntos",
                "Alcanzaste 100 puntos acumulados en QhuriNet.");
        crearLogroSiNoExiste(generadas, u, puntos >= 500, "Logro: 500 puntos",
                "Alcanzaste 500 puntos acumulados en QhuriNet.");

        List<Object[]> estadisticas = uS.estadisticasResumenUsuario(idUsuario);
        if (!estadisticas.isEmpty()) {
            Object[] fila = estadisticas.get(0);
            long recolecciones = toLong(fila[5]) + toLong(fila[6]);
            double kg = toDouble(fila[7]);
            crearLogroSiNoExiste(generadas, u, recolecciones >= 1, "Logro: primera recoleccion",
                    "Completaste tu primera recoleccion en QhuriNet.");
            crearLogroSiNoExiste(generadas, u, recolecciones >= 10, "Logro: 10 recolecciones",
                    "Completaste 10 recolecciones en QhuriNet.");
            crearLogroSiNoExiste(generadas, u, kg >= 10, "Logro: 10 kg reciclados",
                    "Llegaste a 10 kg reciclados registrados.");
        }

        if (generadas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay logros nuevos para notificar");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(generadas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isNotificacionOwner(#id)")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        Optional<Notificacion> notificacion = nS.listId(id);
        if (notificacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificacion no encontrada");
        }

        return ResponseEntity.ok(toDto(notificacion.get()));
    }

    @PatchMapping("/{id}/leida")
    @PreAuthorize("@securityPermissionService.isNotificacionOwner(#id)")
    public ResponseEntity<?> marcarLeida(@PathVariable UUID id,
                                         @RequestParam(value = "leida", required = false) Boolean leida) {
        Optional<Notificacion> notificacion = nS.listId(id);
        if (notificacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificacion no encontrada");
        }

        Notificacion n = notificacion.get();
        n.setLeida(leida == null || leida);
        nS.update(n);
        return ResponseEntity.ok(toDto(n));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizarEstado(@PathVariable UUID id,
                                              @RequestParam("estado") String estado,
                                              @RequestParam(value = "error", required = false) String error) {
        Optional<Notificacion> notificacion = nS.listId(id);
        if (notificacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificacion no encontrada");
        }
        if (isBlank(estado)) {
            return ResponseEntity.badRequest().body("Estado obligatorio");
        }

        Notificacion n = notificacion.get();
        n.setEstado(estado);
        n.setErrorMensaje(error);
        if ("enviada".equalsIgnoreCase(estado)) {
            n.setEnviadaAt(LocalDateTime.now());
        }
        nS.update(n);
        return ResponseEntity.ok(toDto(n));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isNotificacionOwner(#id)")
    public ResponseEntity<?> eliminar(@PathVariable UUID id) {
        if (nS.listId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificacion no encontrada");
        }

        nS.delete(id);
        return ResponseEntity.noContent().build();
    }

    private NotificacionDTO toDto(Notificacion n) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(n.getId());
        dto.setIdUsuario(n.getUsuario().getId());
        dto.setTipo(n.getTipo());
        dto.setTitulo(n.getTitulo());
        dto.setMensaje(n.getMensaje());
        dto.setLeida(n.getLeida());
        dto.setEstado(n.getEstado());
        dto.setErrorMensaje(n.getErrorMensaje());
        dto.setEnviadaAt(n.getEnviadaAt());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    private void crearLogroSiNoExiste(List<NotificacionDTO> generadas, Usuario usuario, boolean condicion, String titulo, String mensaje) {
        if (!condicion || nS.existsByUsuarioTipoTitulo(usuario.getId(), "logro", titulo)) {
            return;
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo("logro");
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setEstado("pendiente");
        generadas.add(toDto(nS.insert(notificacion)));
    }

    private Long toLong(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }

    private Double toDouble(Object value) {
        return value == null ? 0.0 : ((Number) value).doubleValue();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String maskToken(String token) {
        String value = token == null ? "" : token.trim();
        if (value.length() <= 8) {
            return "****";
        }
        return value.substring(0, 4) + "..." + value.substring(value.length() - 4);
    }
}
