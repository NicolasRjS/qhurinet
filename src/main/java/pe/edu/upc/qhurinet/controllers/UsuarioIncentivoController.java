package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.CanjeIncentivoDTO;
import pe.edu.upc.qhurinet.dtos.ProgresoIncentivoDTO;
import pe.edu.upc.qhurinet.dtos.RecordatorioIncentivoDTO;
import pe.edu.upc.qhurinet.dtos.UsuarioIncentivoDTO;
import pe.edu.upc.qhurinet.entities.Incentivo;
import pe.edu.upc.qhurinet.entities.Notificacion;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.entities.UsuarioIncentivo;
import pe.edu.upc.qhurinet.servicesimplements.NivelParticipacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IIncentivoService;
import pe.edu.upc.qhurinet.servicesinterfaces.INotificacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionPuntosService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioIncentivoService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

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
@RequestMapping("/api/usuarios-incentivos")
public class UsuarioIncentivoController {
    private static final String REFERENCIA_RECOMPENSA_DIARIA = "recompensa_diaria";
    private static final String REFERENCIA_DESAFIO = "desafio";
    private static final int PUNTOS_RECOMPENSA_DIARIA = 10;
    private static final int PUNTOS_DESAFIO_DEFAULT = 50;

    @Autowired
    private IUsuarioIncentivoService uS;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IIncentivoService incentivoService;

    @Autowired
    private ITransaccionPuntosService transaccionPuntosService;

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private NivelParticipacionService nivelParticipacionService;

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UsuarioIncentivoDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<UsuarioIncentivoDTO> lista = uS.list()
                .stream()
                .map(y -> {
                    UsuarioIncentivoDTO dto = m.map(y, UsuarioIncentivoDTO.class);
                    dto.setIdUsuario(y.getUsuario().getId());
                    dto.setIdIncentivo(y.getIncentivo().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#dto.idUsuario)")
    public ResponseEntity<?> registrar(@RequestBody UsuarioIncentivoDTO dto) {
        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        Optional<Incentivo> incentivo = incentivoService.listId(dto.getIdIncentivo());

        if (incentivo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incentivo no encontrado");
        }

        ModelMapper m = new ModelMapper();
        UsuarioIncentivo u = m.map(dto, UsuarioIncentivo.class);
        u.setUsuario(usuario.get());
        u.setIncentivo(incentivo.get());

        UsuarioIncentivo cur = uS.insert(u);
        UsuarioIncentivoDTO responseDTO = m.map(cur, UsuarioIncentivoDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        responseDTO.setIdIncentivo(cur.getIncentivo().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isUsuarioIncentivoOwner(#id)")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<UsuarioIncentivo> usu = uS.listId(id);

        if (usu.isPresent()) {
            UsuarioIncentivoDTO dto = m.map(usu.get(), UsuarioIncentivoDTO.class);
            dto.setIdUsuario(usu.get().getUsuario().getId());
            dto.setIdIncentivo(usu.get().getIncentivo().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario incentivo no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("@securityPermissionService.isUsuarioIncentivoOwner(#dto.id)")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioIncentivoDTO dto) {
        Optional<UsuarioIncentivo> existente = uS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario incentivo no encontrado");
        }

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        Optional<Incentivo> incentivo = incentivoService.listId(dto.getIdIncentivo());

        if (incentivo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incentivo no encontrado");
        }

        UsuarioIncentivo u = existente.get();
        u.setUsuario(usuario.get());
        u.setIncentivo(incentivo.get());
        u.setCantidadActual(dto.getCantidadActual());
        u.setEstado(dto.getEstado());
        u.setCompletadoEn(dto.getCompletadoEn());

        uS.update(u);

        return ResponseEntity.ok("Usuario incentivo actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isUsuarioIncentivoOwner(#id)")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<UsuarioIncentivo> usuarioIncentivo = uS.listId(id);

        if (usuarioIncentivo.isPresent()) {
            uS.delete(id);
            return ResponseEntity.ok("Usuario incentivo eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario incentivo no encontrado");
        }
    }

    @GetMapping("/estado/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> estadoIncentivos(@PathVariable UUID idUsuario) {
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("recompensaDiaria", estadoRecompensaDiaria(idUsuario));
        response.put("desafios", progresoDesafios(idUsuario));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recompensa-diaria/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> recompensaDiaria(@PathVariable UUID idUsuario) {
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        return ResponseEntity.ok(estadoRecompensaDiaria(idUsuario));
    }

    @Transactional
    @PostMapping("/recompensa-diaria/{idUsuario}/reclamar")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> reclamarRecompensaDiaria(@PathVariable UUID idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioService.listId(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        if (recompensaDiariaReclamadaHoy(idUsuario)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(estadoRecompensaDiaria(idUsuario));
        }

        int nuevaRacha = calcularRacha(idUsuario, LocalDate.now().minusDays(1)) + 1;
        Usuario usuario = usuarioOpt.get();
        usuario.setPuntosTotales((usuario.getPuntosTotales() == null ? 0 : usuario.getPuntosTotales()) + PUNTOS_RECOMPENSA_DIARIA);
        usuarioService.update(usuario);

        TransaccionPuntos transaccion = new TransaccionPuntos();
        transaccion.setUsuario(usuario);
        transaccion.setTipo("ganado");
        transaccion.setPuntos(PUNTOS_RECOMPENSA_DIARIA);
        transaccion.setMotivo("Recompensa diaria por racha de " + nuevaRacha + " dias");
        transaccion.setReferenciaTipo(REFERENCIA_RECOMPENSA_DIARIA);
        transaccion.setReferenciaId(usuario.getId());
        transaccionPuntosService.insert(transaccion);

        crearNotificacion(usuario, "logro", "Recompensa diaria reclamada",
                "Ganaste " + PUNTOS_RECOMPENSA_DIARIA + " puntos. Racha actual: " + nuevaRacha + " dias.");
        nivelParticipacionService.actualizarNivelSiCorresponde(usuario);

        Map<String, Object> response = estadoRecompensaDiaria(idUsuario);
        response.put("puntosOtorgados", PUNTOS_RECOMPENSA_DIARIA);
        response.put("mensaje", "Recompensa diaria reclamada con exito");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/desafios/{idUsuarioIncentivo}/completar")
    @PreAuthorize("@securityPermissionService.isUsuarioIncentivoOwner(#idUsuarioIncentivo)")
    public ResponseEntity<?> completarDesafio(@PathVariable UUID idUsuarioIncentivo) {
        Optional<UsuarioIncentivo> usuarioIncentivoOpt = uS.listId(idUsuarioIncentivo);
        if (usuarioIncentivoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario incentivo no encontrado");
        }

        UsuarioIncentivo usuarioIncentivo = usuarioIncentivoOpt.get();
        if (!esDesafio(usuarioIncentivo)) {
            return ResponseEntity.badRequest().body("El incentivo indicado no es un desafio");
        }

        Integer meta = usuarioIncentivo.getIncentivo().getMetaCantidad();
        if (meta != null) {
            usuarioIncentivo.setCantidadActual(meta);
        }
        usuarioIncentivo.setEstado("completado");
        usuarioIncentivo.setCompletadoEn(null);
        uS.update(usuarioIncentivo);
        return ResponseEntity.ok(toDto(usuarioIncentivo));
    }

    @Transactional
    @PostMapping("/desafios/{idUsuarioIncentivo}/reclamar")
    @PreAuthorize("@securityPermissionService.isUsuarioIncentivoOwner(#idUsuarioIncentivo)")
    public ResponseEntity<?> reclamarDesafio(@PathVariable UUID idUsuarioIncentivo) {
        Optional<UsuarioIncentivo> usuarioIncentivoOpt = uS.listId(idUsuarioIncentivo);
        if (usuarioIncentivoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario incentivo no encontrado");
        }

        UsuarioIncentivo usuarioIncentivo = usuarioIncentivoOpt.get();
        if (!esDesafio(usuarioIncentivo)) {
            return ResponseEntity.badRequest().body("El incentivo indicado no es un desafio");
        }
        if (usuarioIncentivo.getCompletadoEn() != null || "reclamado".equalsIgnoreCase(usuarioIncentivo.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El desafio ya fue reclamado");
        }
        if (!desafioCompletado(usuarioIncentivo)) {
            return ResponseEntity.badRequest().body("El desafio todavia no esta completado");
        }

        Usuario usuario = usuarioIncentivo.getUsuario();
        int puntos = puntosDesafio(usuarioIncentivo);
        usuario.setPuntosTotales((usuario.getPuntosTotales() == null ? 0 : usuario.getPuntosTotales()) + puntos);
        usuarioService.update(usuario);

        usuarioIncentivo.setEstado("reclamado");
        usuarioIncentivo.setCompletadoEn(LocalDateTime.now());
        UsuarioIncentivo guardado = actualizarYRetornar(usuarioIncentivo);

        TransaccionPuntos transaccion = new TransaccionPuntos();
        transaccion.setUsuario(usuario);
        transaccion.setTipo("ganado");
        transaccion.setPuntos(puntos);
        transaccion.setMotivo("Desafio completado: " + usuarioIncentivo.getIncentivo().getNombre());
        transaccion.setReferenciaTipo(REFERENCIA_DESAFIO);
        transaccion.setReferenciaId(usuarioIncentivo.getId());
        transaccionPuntosService.insert(transaccion);

        crearNotificacion(usuario, "logro", "Desafio completado",
                "Ganaste " + puntos + " puntos por completar " + usuarioIncentivo.getIncentivo().getNombre() + ".");
        nivelParticipacionService.actualizarNivelSiCorresponde(usuario);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("puntosOtorgados", puntos);
        response.put("desafio", toDto(guardado));
        response.put("mensaje", "Puntos de desafio reclamados con exito");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/progreso/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> progresoIncentivos(@PathVariable UUID idUsuario) {
        List<Object[]> lista = uS.progresoIncentivosUsuario(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<ProgresoIncentivoDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            ProgresoIncentivoDTO dto = new ProgresoIncentivoDTO();
            dto.setIdUsuarioIncentivo(toUuid(fila[0]));
            dto.setIdIncentivo(toUuid(fila[1]));
            dto.setNombreIncentivo((String) fila[2]);
            dto.setTipo((String) fila[3]);
            dto.setMetaCantidad(toInteger(fila[4]));
            dto.setMetaUnidad((String) fila[5]);
            dto.setCantidadActual(toInteger(fila[6]));
            dto.setEstado((String) fila[7]);
            dto.setCompletadoEn(toLocalDateTime(fila[8]));
            dto.setPuedeReclamar(toBoolean(fila[9]));
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/recordatorios/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> recordatoriosIncentivos(@PathVariable UUID idUsuario) {
        List<Object[]> lista = uS.recordatoriosIncentivosUsuario(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<RecordatorioIncentivoDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            RecordatorioIncentivoDTO dto = new RecordatorioIncentivoDTO();
            dto.setIdUsuarioIncentivo(toUuid(fila[0]));
            dto.setIdIncentivo(toUuid(fila[1]));
            dto.setNombreIncentivo((String) fila[2]);
            dto.setTipo((String) fila[3]);
            dto.setMetaCantidad(toInteger(fila[4]));
            dto.setCantidadActual(toInteger(fila[5]));
            dto.setEstado((String) fila[6]);
            dto.setFechaFin(toLocalDate(fila[7]));
            dto.setCompletadoEn(toLocalDateTime(fila[8]));
            dto.setPuedeReclamar(toBoolean(fila[9]));
            dto.setProximoAVencer(toBoolean(fila[10]));
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @Transactional
    @PostMapping("/canjear")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#dto.idUsuario)")
    public ResponseEntity<?> canjearIncentivo(@RequestBody CanjeIncentivoDTO dto) {
        if (dto == null || dto.getIdUsuario() == null || dto.getIdIncentivo() == null) {
            return ResponseEntity.badRequest().body("Usuario e incentivo son obligatorios");
        }

        Optional<Usuario> usuarioOpt = usuarioService.listId(dto.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Optional<Incentivo> incentivoOpt = incentivoService.listId(dto.getIdIncentivo());
        if (incentivoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incentivo no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        Incentivo incentivo = incentivoOpt.get();
        if (Boolean.FALSE.equals(incentivo.getActivo())) {
            return ResponseEntity.badRequest().body("Incentivo no disponible");
        }
        if (incentivo.getCostoPuntos() == null || incentivo.getCostoPuntos() < 0) {
            return ResponseEntity.badRequest().body("Costo de incentivo invalido");
        }
        if (incentivo.getStock() != null && incentivo.getStock() <= 0) {
            return ResponseEntity.badRequest().body("Incentivo sin stock");
        }

        int saldoActual = usuario.getPuntosTotales() == null ? 0 : usuario.getPuntosTotales();
        if (saldoActual < incentivo.getCostoPuntos()) {
            return ResponseEntity.badRequest().body("Puntos insuficientes");
        }

        usuario.setPuntosTotales(saldoActual - incentivo.getCostoPuntos());
        usuarioService.update(usuario);

        if (incentivo.getStock() != null) {
            incentivo.setStock(incentivo.getStock() - 1);
            incentivoService.update(incentivo);
        }

        UsuarioIncentivo usuarioIncentivo = buscarUsuarioIncentivo(usuario.getId(), incentivo.getId())
                .orElseGet(UsuarioIncentivo::new);
        usuarioIncentivo.setUsuario(usuario);
        usuarioIncentivo.setIncentivo(incentivo);
        usuarioIncentivo.setCantidadActual(incentivo.getMetaCantidad() == null ? 1 : incentivo.getMetaCantidad());
        usuarioIncentivo.setEstado("canjeado");
        usuarioIncentivo.setCompletadoEn(LocalDateTime.now());

        UsuarioIncentivo guardado = usuarioIncentivo.getId() == null
                ? uS.insert(usuarioIncentivo)
                : actualizarYRetornar(usuarioIncentivo);

        TransaccionPuntos transaccion = new TransaccionPuntos();
        transaccion.setUsuario(usuario);
        transaccion.setTipo("gastado");
        transaccion.setPuntos(incentivo.getCostoPuntos());
        transaccion.setMotivo("Canje de incentivo: " + incentivo.getNombre());
        transaccion.setReferenciaTipo("incentivo");
        transaccion.setReferenciaId(incentivo.getId());
        transaccionPuntosService.insert(transaccion);

        crearNotificacion(usuario, "incentivo", "Incentivo canjeado",
                "Canjeaste el incentivo " + incentivo.getNombre());
        nivelParticipacionService.actualizarNivelSiCorresponde(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(guardado));
    }

    private Optional<UsuarioIncentivo> buscarUsuarioIncentivo(UUID idUsuario, UUID idIncentivo) {
        return uS.list()
                .stream()
                .filter(ui -> ui.getUsuario() != null
                        && ui.getIncentivo() != null
                        && idUsuario.equals(ui.getUsuario().getId())
                        && idIncentivo.equals(ui.getIncentivo().getId()))
                .findFirst();
    }

    private UsuarioIncentivo actualizarYRetornar(UsuarioIncentivo usuarioIncentivo) {
        uS.update(usuarioIncentivo);
        return usuarioIncentivo;
    }

    private UsuarioIncentivoDTO toDto(UsuarioIncentivo usuarioIncentivo) {
        ModelMapper m = new ModelMapper();
        UsuarioIncentivoDTO dto = m.map(usuarioIncentivo, UsuarioIncentivoDTO.class);
        dto.setIdUsuario(usuarioIncentivo.getUsuario().getId());
        dto.setIdIncentivo(usuarioIncentivo.getIncentivo().getId());
        return dto;
    }

    private void crearNotificacion(Usuario usuario, String tipo, String titulo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacionService.insert(notificacion);
    }

    private Map<String, Object> estadoRecompensaDiaria(UUID idUsuario) {
        boolean reclamadaHoy = recompensaDiariaReclamadaHoy(idUsuario);
        LocalDate inicioRacha = reclamadaHoy ? LocalDate.now() : LocalDate.now().minusDays(1);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idUsuario", idUsuario);
        response.put("estado", reclamadaHoy ? "Ya reclamado" : "Disponible para reclamar");
        response.put("disponibleParaReclamar", !reclamadaHoy);
        response.put("rachaDias", calcularRacha(idUsuario, inicioRacha));
        response.put("puntosDisponibles", reclamadaHoy ? 0 : PUNTOS_RECOMPENSA_DIARIA);
        return response;
    }

    private boolean recompensaDiariaReclamadaHoy(UUID idUsuario) {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        return transaccionPuntosService.existsByUsuarioAndReferenciaTipoBetween(
                idUsuario,
                REFERENCIA_RECOMPENSA_DIARIA,
                inicio,
                inicio.plusDays(1).minusNanos(1)
        );
    }

    private int calcularRacha(UUID idUsuario, LocalDate fechaInicial) {
        LocalDate esperada = fechaInicial;
        int racha = 0;
        LocalDate ultimaContada = null;
        for (TransaccionPuntos transaccion : transaccionPuntosService.listByUsuarioAndReferenciaTipo(idUsuario, REFERENCIA_RECOMPENSA_DIARIA)) {
            if (transaccion.getCreatedAt() == null) {
                continue;
            }
            LocalDate fecha = transaccion.getCreatedAt().toLocalDate();
            if (fecha.equals(ultimaContada)) {
                continue;
            }
            if (fecha.equals(esperada)) {
                racha++;
                ultimaContada = fecha;
                esperada = esperada.minusDays(1);
            } else if (fecha.isBefore(esperada)) {
                break;
            }
        }
        return racha;
    }

    private List<ProgresoIncentivoDTO> progresoDesafios(UUID idUsuario) {
        List<ProgresoIncentivoDTO> respuesta = new ArrayList<>();
        for (Object[] fila : uS.progresoIncentivosUsuario(idUsuario)) {
            String tipo = (String) fila[3];
            if (!"desafio".equalsIgnoreCase(tipo)) {
                continue;
            }
            ProgresoIncentivoDTO dto = new ProgresoIncentivoDTO();
            dto.setIdUsuarioIncentivo(toUuid(fila[0]));
            dto.setIdIncentivo(toUuid(fila[1]));
            dto.setNombreIncentivo((String) fila[2]);
            dto.setTipo(tipo);
            dto.setMetaCantidad(toInteger(fila[4]));
            dto.setMetaUnidad((String) fila[5]);
            dto.setCantidadActual(toInteger(fila[6]));
            dto.setEstado((String) fila[7]);
            dto.setCompletadoEn(toLocalDateTime(fila[8]));
            dto.setPuedeReclamar(toBoolean(fila[9]));
            respuesta.add(dto);
        }
        return respuesta;
    }

    private boolean esDesafio(UsuarioIncentivo usuarioIncentivo) {
        return usuarioIncentivo.getIncentivo() != null
                && "desafio".equalsIgnoreCase(usuarioIncentivo.getIncentivo().getTipo());
    }

    private boolean desafioCompletado(UsuarioIncentivo usuarioIncentivo) {
        if ("completado".equalsIgnoreCase(usuarioIncentivo.getEstado())) {
            return true;
        }
        Integer meta = usuarioIncentivo.getIncentivo().getMetaCantidad();
        return meta != null
                && usuarioIncentivo.getCantidadActual() != null
                && usuarioIncentivo.getCantidadActual() >= meta;
    }

    private int puntosDesafio(UsuarioIncentivo usuarioIncentivo) {
        Integer stockComoPuntosDemo = usuarioIncentivo.getIncentivo().getStock();
        return stockComoPuntosDemo != null && stockComoPuntosDemo > 0
                ? stockComoPuntosDemo
                : PUNTOS_DESAFIO_DEFAULT;
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

    private LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }
        return LocalDate.parse(value.toString());
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
