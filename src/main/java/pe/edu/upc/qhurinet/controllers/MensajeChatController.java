package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.MensajeChatDTO;
import pe.edu.upc.qhurinet.entities.MensajeChat;
import pe.edu.upc.qhurinet.entities.Recoleccion;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IMensajeChatService;
import pe.edu.upc.qhurinet.servicesinterfaces.IRecoleccionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mensajes-chat")
public class MensajeChatController {
    @Autowired
    private IMensajeChatService mS;

    @Autowired
    private IRecoleccionService recoleccionService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<MensajeChatDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<MensajeChatDTO> lista = mS.list()
                .stream()
                .map(y -> {
                    MensajeChatDTO dto = m.map(y, MensajeChatDTO.class);
                    dto.setIdRecoleccion(y.getRecoleccion().getId());
                    dto.setIdRemitente(y.getRemitente().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("@securityPermissionService.canCreateMensajeChat(#dto)")
    public ResponseEntity<?> registrar(@RequestBody MensajeChatDTO dto) {
        Optional<Recoleccion> recoleccion = recoleccionService.listId(dto.getIdRecoleccion());

        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recoleccion no encontrada");
        }

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdRemitente());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Remitente no encontrado");
        }

        ModelMapper m = new ModelMapper();
        MensajeChat mensaje = m.map(dto, MensajeChat.class);
        mensaje.setRecoleccion(recoleccion.get());
        mensaje.setRemitente(usuario.get());

        MensajeChat cur = mS.insert(mensaje);
        MensajeChatDTO responseDTO = m.map(cur, MensajeChatDTO.class);
        responseDTO.setIdRecoleccion(cur.getRecoleccion().getId());
        responseDTO.setIdRemitente(cur.getRemitente().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isMensajeChatParticipant(#id)")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<MensajeChat> mensaje = mS.listId(id);

        if (mensaje.isPresent()) {
            MensajeChatDTO dto = m.map(mensaje.get(), MensajeChatDTO.class);
            dto.setIdRecoleccion(mensaje.get().getRecoleccion().getId());
            dto.setIdRemitente(mensaje.get().getRemitente().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mensaje chat no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("@securityPermissionService.isMensajeChatParticipant(#dto.id)")
    public ResponseEntity<String> actualizar(@RequestBody MensajeChatDTO dto) {
        Optional<MensajeChat> existente = mS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mensaje chat no encontrado");
        }

        Optional<Recoleccion> recoleccion = recoleccionService.listId(dto.getIdRecoleccion());

        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recoleccion no encontrada");
        }

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdRemitente());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Remitente no encontrado");
        }

        MensajeChat mensaje = existente.get();
        mensaje.setRecoleccion(recoleccion.get());
        mensaje.setRemitente(usuario.get());
        mensaje.setContenido(dto.getContenido());
        mensaje.setLeido(dto.getLeido());

        mS.update(mensaje);

        return ResponseEntity.ok("Mensaje chat actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isMensajeChatParticipant(#id)")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<MensajeChat> mensaje = mS.listId(id);

        if (mensaje.isPresent()) {
            mS.delete(id);
            return ResponseEntity.ok("Mensaje chat eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mensaje chat no encontrado");
        }
    }
}
