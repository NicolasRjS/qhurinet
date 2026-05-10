package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.HistorialPuntosSaldoDTO;
import pe.edu.upc.qhurinet.dtos.PuntosMesUsuarioDTO;
import pe.edu.upc.qhurinet.dtos.TransaccionPuntosDTO;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionPuntosService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transacciones-puntos")
public class TransaccionPuntosController {
    @Autowired
    private ITransaccionPuntosService tS;

    @Autowired
    private IUsuarioService uS;

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<TransaccionPuntosDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<TransaccionPuntosDTO> lista = tS.list()
                .stream()
                .map(y -> {
                    TransaccionPuntosDTO dto = m.map(y, TransaccionPuntosDTO.class);
                    dto.setIdUsuario(y.getUsuario().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody TransaccionPuntosDTO dto) {
        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        TransaccionPuntos t = m.map(dto, TransaccionPuntos.class);
        t.setUsuario(usuario.get());

        TransaccionPuntos cur = tS.insert(t);
        TransaccionPuntosDTO responseDTO = m.map(cur, TransaccionPuntosDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isTransaccionPuntosOwner(#id)")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<TransaccionPuntos> transaccion = tS.listId(id);

        if (transaccion.isPresent()) {
            TransaccionPuntosDTO dto = m.map(transaccion.get(), TransaccionPuntosDTO.class);
            dto.setIdUsuario(transaccion.get().getUsuario().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transaccion puntos no encontrada");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody TransaccionPuntosDTO dto) {
        Optional<TransaccionPuntos> existente = tS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transaccion puntos no encontrada");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        TransaccionPuntos t = existente.get();
        t.setUsuario(usuario.get());
        t.setTipo(dto.getTipo());
        t.setPuntos(dto.getPuntos());
        t.setMotivo(dto.getMotivo());
        t.setReferenciaTipo(dto.getReferenciaTipo());
        t.setReferenciaId(dto.getReferenciaId());

        tS.update(t);

        return ResponseEntity.ok("Transaccion puntos actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<TransaccionPuntos> transaccion = tS.listId(id);

        if (transaccion.isPresent()) {
            tS.delete(id);
            return ResponseEntity.ok("Transaccion puntos eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transaccion puntos no encontrada");
        }
    }
    @GetMapping("/total-mes/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> totalPuntosMes(@PathVariable UUID idUsuario,
                                            @RequestParam("mes") String mes) {
        List<Object[]> lista = tS.totalPuntosGanadosPorMes(idUsuario, mes);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }
        Object[] fila = lista.get(0);
        PuntosMesUsuarioDTO dto = new PuntosMesUsuarioDTO();
        dto.setIdUsuario(toUuid(fila[0]));
        dto.setNombre((String) fila[1]);
        dto.setTotalPuntosMes(((Number) fila[2]).intValue());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/historial/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> historialPuntos(@PathVariable UUID idUsuario) {
        List<Object[]> lista = tS.historialPuntosConSaldo(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<HistorialPuntosSaldoDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            HistorialPuntosSaldoDTO dto = new HistorialPuntosSaldoDTO();
            dto.setIdTransaccion(toUuid(fila[0]));
            dto.setCreatedAt(toLocalDateTime(fila[1]));
            dto.setTipo((String) fila[2]);
            dto.setPuntos(toInteger(fila[3]));
            dto.setMotivo((String) fila[4]);
            dto.setReferenciaTipo((String) fila[5]);
            dto.setReferenciaId(toUuid(fila[6]));
            dto.setSaldoAcumulado(toInteger(fila[7]));
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
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
