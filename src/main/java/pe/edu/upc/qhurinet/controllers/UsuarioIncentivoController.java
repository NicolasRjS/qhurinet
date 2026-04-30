package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.UsuarioIncentivoDTO;
import pe.edu.upc.qhurinet.entities.Incentivo;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.entities.UsuarioIncentivo;
import pe.edu.upc.qhurinet.servicesinterfaces.IIncentivoService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioIncentivoService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

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
}
