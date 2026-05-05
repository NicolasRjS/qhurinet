package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.TransaccionDineroDTO;
import pe.edu.upc.qhurinet.entities.TransaccionDinero;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionDineroService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transacciones-dinero")
public class TransaccionDineroController {
    @Autowired
    private ITransaccionDineroService tS;

    @Autowired
    private IUsuarioService uS;

    @GetMapping("/lista")
    public ResponseEntity<List<TransaccionDineroDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<TransaccionDineroDTO> lista = tS.list()
                .stream()
                .map(y -> {
                    TransaccionDineroDTO dto = m.map(y, TransaccionDineroDTO.class);
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
    public ResponseEntity<?> registrar(@RequestBody TransaccionDineroDTO dto) {
        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        TransaccionDinero t = m.map(dto, TransaccionDinero.class);
        t.setUsuario(usuario.get());

        TransaccionDinero cur = tS.insert(t);
        TransaccionDineroDTO responseDTO = m.map(cur, TransaccionDineroDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<TransaccionDinero> transaccion = tS.listId(id);

        if (transaccion.isPresent()) {
            TransaccionDineroDTO dto = m.map(transaccion.get(), TransaccionDineroDTO.class);
            dto.setIdUsuario(transaccion.get().getUsuario().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transaccion dinero no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody TransaccionDineroDTO dto) {
        Optional<TransaccionDinero> existente = tS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transaccion dinero no encontrada");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        TransaccionDinero t = existente.get();
        t.setUsuario(usuario.get());
        t.setTipo(dto.getTipo());
        t.setMonto(dto.getMonto());
        t.setMoneda(dto.getMoneda());
        t.setEstado(dto.getEstado());
        t.setConcepto(dto.getConcepto());
        t.setMetodoPagoTipo(dto.getMetodoPagoTipo());
        t.setMetodoPagoDetalle(dto.getMetodoPagoDetalle());
        t.setReferenciaExterna(dto.getReferenciaExterna());
        t.setReferenciaTipo(dto.getReferenciaTipo());
        t.setReferenciaId(dto.getReferenciaId());

        tS.update(t);

        return ResponseEntity.ok("Transaccion dinero actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<TransaccionDinero> transaccion = tS.listId(id);

        if (transaccion.isPresent()) {
            tS.delete(id);
            return ResponseEntity.ok("Transaccion dinero eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transaccion dinero no encontrada");
        }
    }
}
