package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.UsuarioDTO;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private IUsuarioService uS;

    @GetMapping("/lista")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<UsuarioDTO> lista = uS.list()
                .stream()
                .map(y -> m.map(y, UsuarioDTO.class))
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDTO dto) {
        ModelMapper m = new ModelMapper();
        Usuario u = m.map(dto, Usuario.class);
        Usuario cur = uS.insert(u);
        UsuarioDTO responseDTO = m.map(cur, UsuarioDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Usuario> usu = uS.listId(id);

        if (usu.isPresent()) {
            UsuarioDTO dto = m.map(usu.get(), UsuarioDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioDTO dto) {
        Optional<Usuario> existente = uS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        Usuario u = existente.get();
        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        u.setPasswordHash(dto.getPasswordHash());
        u.setTelefono(dto.getTelefono());
        u.setFotoUrl(dto.getFotoUrl());
        u.setDescripcion(dto.getDescripcion());
        u.setRol(dto.getRol());
        u.setTipoCuenta(dto.getTipoCuenta());
        u.setProveedorAuth(dto.getProveedorAuth());
        u.setDisponible(dto.getDisponible());
        u.setVerificado(dto.getVerificado());
        u.setPuntosTotales(dto.getPuntosTotales());
        u.setNivelParticipacion(dto.getNivelParticipacion());

        uS.update(u);

        return ResponseEntity.ok("Usuario actualizado correctamente");
    }

    @DeleteMapping("/{id}")
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
}
