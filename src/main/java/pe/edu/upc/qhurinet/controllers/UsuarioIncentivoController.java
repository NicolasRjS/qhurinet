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
import pe.edu.upc.qhurinet.servicesinterfaces.IIncentivoService;
import pe.edu.upc.qhurinet.servicesinterfaces.INotificacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionPuntosService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioIncentivoService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios-incentivos")
public class UsuarioIncentivoController {
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

    @GetMapping("/lista")
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
