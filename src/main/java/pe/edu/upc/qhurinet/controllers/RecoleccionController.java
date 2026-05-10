package pe.edu.upc.qhurinet.controllers;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pe.edu.upc.qhurinet.dtos.ActividadDetalleDTO;
import pe.edu.upc.qhurinet.dtos.DisponibilidadRecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.HistorialRecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.IncidenciaRecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.IncidenciaUsuarioDTO;
import pe.edu.upc.qhurinet.dtos.PromedioRecolectorDTO;
import pe.edu.upc.qhurinet.dtos.QrRecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.RecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.RecoleccionPendienteDTO;
import pe.edu.upc.qhurinet.dtos.RecoleccionRangoDTO;
import pe.edu.upc.qhurinet.dtos.ReprogramarRecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.UbicacionRecolectorHistorialDTO;
import pe.edu.upc.qhurinet.dtos.UbicacionRecolectorDTO;
import pe.edu.upc.qhurinet.dtos.ValidacionQrResponseDTO;
import pe.edu.upc.qhurinet.dtos.ValidarQrDTO;
import pe.edu.upc.qhurinet.entities.Notificacion;
import pe.edu.upc.qhurinet.entities.Publicacion;
import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.Recoleccion;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;
import pe.edu.upc.qhurinet.entities.UbicacionRecolectorHistorial;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesimplements.NivelParticipacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionMaterialService;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.INotificacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IRecoleccionService;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionPuntosService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUbicacionRecolectorHistorialService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recolecciones")
public class RecoleccionController {
    @Autowired
    private IRecoleccionService rS;

    @Autowired
    private IPublicacionService publicacionService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPublicacionMaterialService publicacionMaterialService;

    @Autowired
    private ITransaccionPuntosService transaccionPuntosService;

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private IUbicacionRecolectorHistorialService ubicacionHistorialService;

    @Autowired
    private NivelParticipacionService nivelParticipacionService;

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RecoleccionDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<RecoleccionDTO> lista = rS.list()
                .stream()
                .map(y -> {
                    RecoleccionDTO dto = m.map(y, RecoleccionDTO.class);
                    dto.setIdPublicacion(y.getPublicacion().getId());
                    dto.setIdRecolector(y.getRecolector().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("@securityPermissionService.canCreateRecoleccion(#dto)")
    public ResponseEntity<?> registrar(@RequestBody RecoleccionDTO dto) {
        if (dto == null || dto.getIdPublicacion() == null || dto.getIdRecolector() == null || dto.getFechaProgramada() == null) {
            return ResponseEntity.badRequest().body("Error: faltan datos para programar la recoleccion");
        }
        if (dto.getFechaProgramada().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Fecha y hora invalida");
        }

        Optional<Publicacion> publicacion = publicacionService.listId(dto.getIdPublicacion());

        if (publicacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdRecolector());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recolector no encontrado");
        }

        ModelMapper m = new ModelMapper();
        Recoleccion r = m.map(dto, Recoleccion.class);
        r.setPublicacion(publicacion.get());
        r.setRecolector(usuario.get());
        if (r.getEstado() == null || r.getEstado().isBlank()) {
            r.setEstado("programada");
        }
        if (r.getPrioritaria() == null) {
            r.setPrioritaria(false);
        }
        if (r.getQrValidado() == null) {
            r.setQrValidado(false);
        }
        if (r.getCodigoQr() == null || r.getCodigoQr().isBlank()) {
            r.setCodigoQr(generarCodigoQr());
        }

        Recoleccion cur = rS.insert(r);
        RecoleccionDTO responseDTO = m.map(cur, RecoleccionDTO.class);
        responseDTO.setIdPublicacion(cur.getPublicacion().getId());
        responseDTO.setIdRecolector(cur.getRecolector().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Recoleccion> rec = rS.listId(id);

        if (rec.isPresent()) {
            RecoleccionDTO dto = m.map(rec.get(), RecoleccionDTO.class);
            dto.setIdPublicacion(rec.get().getPublicacion().getId());
            dto.setIdRecolector(rec.get().getRecolector().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recoleccion no encontrada");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#dto)")
    public ResponseEntity<String> actualizar(@RequestBody RecoleccionDTO dto) {
        Optional<Recoleccion> existente = rS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recoleccion no encontrada");
        }

        Optional<Publicacion> publicacion = publicacionService.listId(dto.getIdPublicacion());

        if (publicacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdRecolector());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recolector no encontrado");
        }

        Recoleccion r = existente.get();
        r.setPublicacion(publicacion.get());
        r.setRecolector(usuario.get());
        r.setEstado(dto.getEstado());
        r.setFechaProgramada(dto.getFechaProgramada());
        r.setFechaCompletada(dto.getFechaCompletada());
        r.setPrioritaria(dto.getPrioritaria());
        r.setCodigoQr(dto.getCodigoQr());
        r.setQrValidado(dto.getQrValidado());
        r.setLatRecolector(dto.getLatRecolector());
        r.setLngRecolector(dto.getLngRecolector());
        r.setIncidenciaDescripcion(dto.getIncidenciaDescripcion());
        r.setIncidenciaEstado(dto.getIncidenciaEstado());
        r.setIncidenciaEvidenciaUrl(dto.getIncidenciaEvidenciaUrl());

        rS.update(r);

        return ResponseEntity.ok("Recoleccion actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Recoleccion> recoleccion = rS.listId(id);

        if (recoleccion.isPresent()) {
            ubicacionHistorialService.deleteByRecoleccion(id);
            rS.delete(id);
            return ResponseEntity.ok("Recoleccion eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recoleccion no encontrada");
        }
    }

    @GetMapping(value = "/{id}/comprobante.csv", produces = "text/csv")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> descargarComprobanteCsv(@PathVariable UUID id) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }

        String csv = generarComprobanteCsv(recoleccion.get());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"comprobante-recoleccion-" + id + ".csv\"")
                .contentType(new MediaType("text", "csv"))
                .body(csv);
    }

    @GetMapping(value = "/{id}/comprobante.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> descargarComprobantePdf(@PathVariable UUID id) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }

        try {
            byte[] pdf = generarComprobantePdf(recoleccion.get());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"comprobante-recoleccion-" + id + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (DocumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo generar el PDF");
        }
    }

    @GetMapping(value = "/historial/{idUsuario}/comprobante.csv", produces = "text/csv")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> descargarHistorialCsv(@PathVariable UUID idUsuario,
                                                   @RequestParam(value = "fechaIni", required = false) LocalDate fechaIni,
                                                   @RequestParam(value = "fechaFin", required = false) LocalDate fechaFin,
                                                   @RequestParam(value = "estado", required = false) String estado) {
        List<Object[]> lista = rS.actividadesDetalle(idUsuario, fechaIni, fechaFin, estado);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        String csv = generarHistorialCsv(lista);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"historial-recolecciones-" + idUsuario + ".csv\"")
                .contentType(new MediaType("text", "csv"))
                .body(csv);
    }

    @PatchMapping("/{id}/reprogramar")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> reprogramar(@PathVariable UUID id,
                                         @RequestBody ReprogramarRecoleccionDTO dto) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }
        if (dto == null || dto.getFechaProgramada() == null || dto.getFechaProgramada().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Fecha y hora invalida");
        }

        Recoleccion r = recoleccion.get();
        r.setFechaProgramada(dto.getFechaProgramada());
        if ("cancelada".equalsIgnoreCase(r.getEstado())) {
            r.setEstado("programada");
        }
        rS.update(r);
        return ResponseEntity.ok(toDto(r));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> cancelar(@PathVariable UUID id) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }

        Recoleccion r = recoleccion.get();
        r.setEstado("cancelada");
        rS.update(r);
        crearNotificacion(r.getRecolector(), "recoleccion", "Recoleccion cancelada",
                "La recoleccion " + r.getId() + " fue cancelada.");
        return ResponseEntity.ok(toDto(r));
    }

    @PatchMapping("/{id}/prioridad")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> cambiarPrioridad(@PathVariable UUID id,
                                             @RequestParam(value = "prioritaria", required = false) Boolean prioritaria) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }

        Recoleccion r = recoleccion.get();
        boolean nuevoValor = prioritaria != null ? prioritaria : !Boolean.TRUE.equals(r.getPrioritaria());
        r.setPrioritaria(nuevoValor);
        rS.update(r);
        return ResponseEntity.ok(toDto(r));
    }

    @PatchMapping("/{id}/incidencia")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> reportarIncidencia(@PathVariable UUID id,
                                                @RequestBody IncidenciaRecoleccionDTO dto) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }
        if (dto == null || dto.getDescripcion() == null || dto.getDescripcion().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Error al registrar la incidencia, completa todos los campos");
        }

        Recoleccion r = recoleccion.get();
        r.setIncidenciaDescripcion(dto.getDescripcion().trim());
        r.setIncidenciaEvidenciaUrl(dto.getEvidenciaUrl());
        r.setIncidenciaEstado(dto.getEstado() == null || dto.getEstado().isBlank() ? "abierta" : dto.getEstado());
        rS.update(r);
        return ResponseEntity.ok(toDto(r));
    }

    @PatchMapping("/{id}/ubicacion-recolector")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> actualizarUbicacionRecolector(@PathVariable UUID id,
                                                           @RequestBody UbicacionRecolectorDTO dto) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }
        if (dto == null || dto.getLatRecolector() == null || dto.getLngRecolector() == null) {
            return ResponseEntity.badRequest().body("Latitud y longitud son obligatorias");
        }

        Recoleccion r = recoleccion.get();
        r.setLatRecolector(dto.getLatRecolector());
        r.setLngRecolector(dto.getLngRecolector());
        rS.update(r);
        registrarUbicacionHistorial(r, dto);
        return ResponseEntity.ok(toDto(r));
    }

    @GetMapping("/{id}/ubicacion-recolector")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> obtenerUbicacionRecolector(@PathVariable UUID id) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }

        Recoleccion r = recoleccion.get();
        UbicacionRecolectorDTO dto = new UbicacionRecolectorDTO();
        dto.setLatRecolector(r.getLatRecolector());
        dto.setLngRecolector(r.getLngRecolector());
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}/ubicacion-recolector/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> streamUbicacionRecolector(@PathVariable UUID id) {
        if (rS.listId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }

        SseEmitter emitter = new SseEmitter(30000L);
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < 6; i++) {
                    Optional<Recoleccion> actual = rS.listId(id);
                    if (actual.isEmpty()) {
                        emitter.complete();
                        return;
                    }
                    Recoleccion r = actual.get();
                    UbicacionRecolectorDTO dto = new UbicacionRecolectorDTO();
                    dto.setLatRecolector(r.getLatRecolector());
                    dto.setLngRecolector(r.getLngRecolector());
                    emitter.send(SseEmitter.event().name("ubicacion").data(dto));
                    Thread.sleep(5000L);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return ResponseEntity.ok(emitter);
    }

    @GetMapping("/{id}/ubicaciones-recolector")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> historialUbicacionesRecolector(@PathVariable UUID id) {
        List<UbicacionRecolectorHistorialDTO> lista = ubicacionHistorialService.listByRecoleccion(id)
                .stream()
                .map(this::toUbicacionHistorialDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}/qr")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> obtenerQr(@PathVariable UUID id) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }

        Recoleccion r = recoleccion.get();
        if (r.getCodigoQr() == null || r.getCodigoQr().isBlank()) {
            r.setCodigoQr(generarCodigoQr());
            rS.update(r);
        }

        QrRecoleccionDTO dto = new QrRecoleccionDTO();
        dto.setIdRecoleccion(r.getId());
        dto.setCodigoQr(r.getCodigoQr());
        dto.setQrValidado(r.getQrValidado());
        return ResponseEntity.ok(dto);
    }

    @Transactional
    @PostMapping("/{id}/validar-qr")
    @PreAuthorize("@securityPermissionService.isRecoleccionParticipant(#id)")
    public ResponseEntity<?> validarQr(@PathVariable UUID id,
                                       @RequestBody ValidarQrDTO dto) {
        Optional<Recoleccion> recoleccion = rS.listId(id);
        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recoleccion no encontrada");
        }
        if (dto == null || dto.getCodigoQr() == null || dto.getCodigoQr().isBlank()) {
            return ResponseEntity.badRequest().body("Codigo QR obligatorio");
        }

        Recoleccion r = recoleccion.get();
        if (r.getCodigoQr() == null || !r.getCodigoQr().equals(dto.getCodigoQr().trim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Codigo QR invalido");
        }

        boolean yaValidado = Boolean.TRUE.equals(r.getQrValidado());
        r.setQrValidado(true);
        r.setEstado("completada");
        if (r.getFechaCompletada() == null) {
            r.setFechaCompletada(LocalDateTime.now());
        }
        rS.update(r);

        int puntosAcreditados = yaValidado ? 0 : acreditarPuntosPorRecoleccion(r);
        if (r.getPublicacion() != null) {
            Publicacion publicacion = r.getPublicacion();
            publicacion.setEstado("recolectada");
            publicacionService.update(publicacion);
            if (!yaValidado) {
                crearNotificacion(publicacion.getUsuario(), "puntos", "Puntos acreditados",
                        "Se acreditaron " + puntosAcreditados + " puntos por una recoleccion completada.");
                crearNotificacion(r.getRecolector(), "recoleccion", "Recoleccion completada",
                        "La entrega fue confirmada correctamente.");
            }
        }

        ValidacionQrResponseDTO response = new ValidacionQrResponseDTO();
        response.setIdRecoleccion(r.getId());
        response.setEstado(r.getEstado());
        response.setQrValidado(r.getQrValidado());
        response.setFechaCompletada(r.getFechaCompletada());
        response.setPuntosAcreditados(puntosAcreditados);
        return ResponseEntity.ok(response);
    }

    //Historial completo de un usuario por ID
    @GetMapping("/historial/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> historialUsuario(@PathVariable UUID idUsuario,
                                              @RequestParam(value = "fechaIni", required = false) LocalDate fechaIni,
                                              @RequestParam(value = "fechaFin", required = false) LocalDate fechaFin,
                                              @RequestParam(value = "estado", required = false) String estado) {
        List<Object[]> lista = rS.historialUsuario(idUsuario, fechaIni, fechaFin, estado);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<HistorialRecoleccionDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            HistorialRecoleccionDTO dto = new HistorialRecoleccionDTO();
            dto.setId(toUuid(fila[0]));
            dto.setFechaProgramada((LocalDateTime) fila[1]);
            dto.setEstado((String) fila[2]);
            dto.setTituloPublicacion((String) fila[3]);
            dto.setRolUsuario((String) fila[4]);
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/promedio-recolector/{idRecolector}")
    public ResponseEntity<?> promedioRecolector(@PathVariable UUID idRecolector) {
        List<Object[]> lista = rS.promedioCalificacionRecolector(idRecolector);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        Object[] fila = lista.get(0);

        PromedioRecolectorDTO dto = new PromedioRecolectorDTO();
        dto.setIdRecolector(toUuid(fila[0]));
        dto.setNombreRecolector((String) fila[1]);
        dto.setPuntuacionPromedio(((Number) fila[2]).doubleValue());
        dto.setTotalRecolecciones(((Number) fila[3]).longValue());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/rango")
    public ResponseEntity<?> recoleccionesPorRango(@RequestParam("fechaIni") LocalDate fechaIni,
                                                   @RequestParam("fechaFin") LocalDate fechaFin,
                                                   @RequestParam(value = "estado", required = false) String estado) {
        List<Object[]> lista = rS.recoleccionesPorRangoYEstado(fechaIni, fechaFin, estado);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<RecoleccionRangoDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            RecoleccionRangoDTO dto = new RecoleccionRangoDTO();
            dto.setId(toUuid(fila[0]));
            dto.setFechaProgramada((LocalDateTime) fila[1]);
            dto.setFechaCompletada((LocalDateTime) fila[2]);
            dto.setEstado((String) fila[3]);
            dto.setTituloPublicacion((String) fila[4]);
            dto.setNombreRecolector((String) fila[5]);
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidadPorFecha(@RequestParam("fecha") LocalDate fecha) {
        List<Object[]> lista = rS.disponibilidadPorFecha(fecha);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<DisponibilidadRecoleccionDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            DisponibilidadRecoleccionDTO dto = new DisponibilidadRecoleccionDTO();
            dto.setIdRecoleccion(toUuid(fila[0]));
            dto.setFechaProgramada(toLocalDateTime(fila[1]));
            dto.setEstado((String) fila[2]);
            dto.setPrioritaria(toBoolean(fila[3]));
            dto.setIdPublicacion(toUuid(fila[4]));
            dto.setTituloPublicacion((String) fila[5]);
            dto.setIdRecolector(toUuid(fila[6]));
            dto.setNombreRecolector((String) fila[7]);
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/recolector/{idRecolector}/pendientes")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idRecolector)")
    public ResponseEntity<?> recoleccionesPendientesRecolector(@PathVariable UUID idRecolector) {
        List<Object[]> lista = rS.recoleccionesPendientesRecolector(idRecolector);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<RecoleccionPendienteDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            RecoleccionPendienteDTO dto = new RecoleccionPendienteDTO();
            dto.setIdRecoleccion(toUuid(fila[0]));
            dto.setIdPublicacion(toUuid(fila[1]));
            dto.setTituloPublicacion((String) fila[2]);
            dto.setDireccionReferencia((String) fila[3]);
            dto.setLatitud(toDouble(fila[4]));
            dto.setLongitud(toDouble(fila[5]));
            dto.setFechaProgramada(toLocalDateTime(fila[6]));
            dto.setPrioritaria(toBoolean(fila[7]));
            dto.setEstado((String) fila[8]);
            dto.setNombreEmisor((String) fila[9]);
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/incidencias/usuario/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> incidenciasUsuario(@PathVariable UUID idUsuario) {
        List<Object[]> lista = rS.incidenciasUsuario(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<IncidenciaUsuarioDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            IncidenciaUsuarioDTO dto = new IncidenciaUsuarioDTO();
            dto.setIdRecoleccion(toUuid(fila[0]));
            dto.setIdPublicacion(toUuid(fila[1]));
            dto.setTituloPublicacion((String) fila[2]);
            dto.setIncidenciaDescripcion((String) fila[3]);
            dto.setIncidenciaEstado((String) fila[4]);
            dto.setIncidenciaEvidenciaUrl((String) fila[5]);
            dto.setEstadoRecoleccion((String) fila[6]);
            dto.setFechaProgramada(toLocalDateTime(fila[7]));
            dto.setIdRecolector(toUuid(fila[8]));
            dto.setNombreRecolector((String) fila[9]);
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/actividades-detalle")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> actividadesDetalle(@RequestParam("idUsuario") UUID idUsuario,
                                                @RequestParam(value = "fechaIni", required = false) LocalDate fechaIni,
                                                @RequestParam(value = "fechaFin", required = false) LocalDate fechaFin,
                                                @RequestParam(value = "estado", required = false) String estado) {
        List<Object[]> lista = rS.actividadesDetalle(idUsuario, fechaIni, fechaFin, estado);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<ActividadDetalleDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            ActividadDetalleDTO dto = new ActividadDetalleDTO();
            dto.setIdRecoleccion(toUuid(fila[0]));
            dto.setFechaProgramada(toLocalDateTime(fila[1]));
            dto.setFechaCompletada(toLocalDateTime(fila[2]));
            dto.setEstado((String) fila[3]);
            dto.setIdPublicacion(toUuid(fila[4]));
            dto.setTituloPublicacion((String) fila[5]);
            dto.setMaterial((String) fila[6]);
            dto.setCantidad(toDouble(fila[7]));
            dto.setUnidad((String) fila[8]);
            dto.setRolUsuario((String) fila[9]);
            dto.setIdContraparte(toUuid(fila[10]));
            dto.setNombreContraparte((String) fila[11]);
            dto.setTieneIncidencia(toBoolean(fila[12]));
            dto.setIncidenciaEstado((String) fila[13]);
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    private RecoleccionDTO toDto(Recoleccion r) {
        ModelMapper m = new ModelMapper();
        RecoleccionDTO dto = m.map(r, RecoleccionDTO.class);
        if (r.getPublicacion() != null) {
            dto.setIdPublicacion(r.getPublicacion().getId());
        }
        if (r.getRecolector() != null) {
            dto.setIdRecolector(r.getRecolector().getId());
        }
        return dto;
    }

    private String generarCodigoQr() {
        return UUID.randomUUID().toString();
    }

    private int acreditarPuntosPorRecoleccion(Recoleccion r) {
        if (r.getPublicacion() == null || r.getPublicacion().getUsuario() == null) {
            return 0;
        }

        BigDecimal puntosCalculados = publicacionMaterialService.puntosPorPublicacion(r.getPublicacion().getId());
        int puntos = puntosCalculados == null ? 0 : puntosCalculados.setScale(0, RoundingMode.HALF_UP).intValue();
        if (puntos <= 0) {
            return 0;
        }

        Usuario usuario = r.getPublicacion().getUsuario();
        int saldoActual = usuario.getPuntosTotales() == null ? 0 : usuario.getPuntosTotales();
        usuario.setPuntosTotales(saldoActual + puntos);
        usuarioService.update(usuario);
        nivelParticipacionService.actualizarNivelSiCorresponde(usuario);

        TransaccionPuntos transaccion = new TransaccionPuntos();
        transaccion.setUsuario(usuario);
        transaccion.setTipo("ganado");
        transaccion.setPuntos(puntos);
        transaccion.setMotivo("Recoleccion completada");
        transaccion.setReferenciaTipo("recoleccion");
        transaccion.setReferenciaId(r.getId());
        transaccionPuntosService.insert(transaccion);

        return puntos;
    }

    private String generarComprobanteCsv(Recoleccion r) {
        StringBuilder csv = new StringBuilder();
        csv.append("campo,valor\n");
        csv.append("id_recoleccion,").append(csv(r.getId())).append("\n");
        csv.append("estado,").append(csv(r.getEstado())).append("\n");
        csv.append("fecha_programada,").append(csv(r.getFechaProgramada())).append("\n");
        csv.append("fecha_completada,").append(csv(r.getFechaCompletada())).append("\n");
        csv.append("id_publicacion,").append(csv(r.getPublicacion() == null ? null : r.getPublicacion().getId())).append("\n");
        csv.append("publicacion,").append(csv(r.getPublicacion() == null ? null : r.getPublicacion().getTitulo())).append("\n");
        csv.append("direccion,").append(csv(r.getPublicacion() == null ? null : r.getPublicacion().getDireccionReferencia())).append("\n");
        csv.append("id_recolector,").append(csv(r.getRecolector() == null ? null : r.getRecolector().getId())).append("\n");
        csv.append("recolector,").append(csv(r.getRecolector() == null ? null : r.getRecolector().getNombre())).append("\n");

        if (r.getPublicacion() != null) {
            for (PublicacionMaterial pm : publicacionMaterialService.listByPublicacion(r.getPublicacion().getId())) {
                String material = pm.getMaterial() == null ? null : pm.getMaterial().getNombre();
                csv.append("material,").append(csv(material)).append("\n");
                csv.append("cantidad,").append(csv(pm.getCantidad())).append("\n");
                csv.append("unidad,").append(csv(pm.getUnidad())).append("\n");
            }
        }
        return csv.toString();
    }

    private byte[] generarComprobantePdf(Recoleccion r) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        document.add(new Paragraph("Comprobante de recoleccion QhuriNet", titleFont));
        document.add(new Paragraph(" "));
        pdfLine(document, "ID recoleccion", r.getId());
        pdfLine(document, "Estado", r.getEstado());
        pdfLine(document, "Fecha programada", r.getFechaProgramada());
        pdfLine(document, "Fecha completada", r.getFechaCompletada());
        pdfLine(document, "Publicacion", r.getPublicacion() == null ? null : r.getPublicacion().getTitulo());
        pdfLine(document, "Direccion", r.getPublicacion() == null ? null : r.getPublicacion().getDireccionReferencia());
        pdfLine(document, "Recolector", r.getRecolector() == null ? null : r.getRecolector().getNombre());

        if (r.getPublicacion() != null) {
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Materiales", sectionFont));
            for (PublicacionMaterial pm : publicacionMaterialService.listByPublicacion(r.getPublicacion().getId())) {
                String material = pm.getMaterial() == null ? "Material" : pm.getMaterial().getNombre();
                document.add(new Paragraph(material + " - " + value(pm.getCantidad()) + " " + value(pm.getUnidad())));
            }
        }

        document.close();
        return out.toByteArray();
    }

    private void pdfLine(Document document, String label, Object value) throws DocumentException {
        document.add(new Paragraph(label + ": " + value(value)));
    }

    private String generarHistorialCsv(List<Object[]> filas) {
        StringBuilder csv = new StringBuilder();
        csv.append("id_recoleccion,fecha_programada,fecha_completada,estado,id_publicacion,titulo_publicacion,material,cantidad,unidad,rol_usuario,id_contraparte,nombre_contraparte,tiene_incidencia,incidencia_estado\n");
        for (Object[] fila : filas) {
            csv.append(csv(toUuid(fila[0]))).append(",");
            csv.append(csv(toLocalDateTime(fila[1]))).append(",");
            csv.append(csv(toLocalDateTime(fila[2]))).append(",");
            csv.append(csv(fila[3])).append(",");
            csv.append(csv(toUuid(fila[4]))).append(",");
            csv.append(csv(fila[5])).append(",");
            csv.append(csv(fila[6])).append(",");
            csv.append(csv(toDouble(fila[7]))).append(",");
            csv.append(csv(fila[8])).append(",");
            csv.append(csv(fila[9])).append(",");
            csv.append(csv(toUuid(fila[10]))).append(",");
            csv.append(csv(fila[11])).append(",");
            csv.append(csv(toBoolean(fila[12]))).append(",");
            csv.append(csv(fila[13])).append("\n");
        }
        return csv.toString();
    }

    private void registrarUbicacionHistorial(Recoleccion r, UbicacionRecolectorDTO dto) {
        UbicacionRecolectorHistorial historial = new UbicacionRecolectorHistorial();
        historial.setRecoleccion(r);
        historial.setLatRecolector(dto.getLatRecolector());
        historial.setLngRecolector(dto.getLngRecolector());
        ubicacionHistorialService.insert(historial);
    }

    private UbicacionRecolectorHistorialDTO toUbicacionHistorialDto(UbicacionRecolectorHistorial historial) {
        UbicacionRecolectorHistorialDTO dto = new UbicacionRecolectorHistorialDTO();
        dto.setId(historial.getId());
        dto.setIdRecoleccion(historial.getRecoleccion().getId());
        dto.setLatRecolector(historial.getLatRecolector());
        dto.setLngRecolector(historial.getLngRecolector());
        dto.setCreatedAt(historial.getCreatedAt());
        return dto;
    }

    private void crearNotificacion(Usuario usuario, String tipo, String titulo, String mensaje) {
        if (usuario == null) {
            return;
        }
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacionService.insert(notificacion);
    }

    private String csv(Object value) {
        if (value == null) {
            return "";
        }
        String text = value.toString().replace("\"", "\"\"");
        return "\"" + text + "\"";
    }

    private String value(Object value) {
        return value == null ? "" : value.toString();
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
