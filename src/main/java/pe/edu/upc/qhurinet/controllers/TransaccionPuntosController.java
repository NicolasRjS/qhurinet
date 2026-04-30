package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.TransaccionPuntosDTO;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionPuntosService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

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
}
