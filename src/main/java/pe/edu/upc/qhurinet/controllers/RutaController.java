package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.RutaDTO;
import pe.edu.upc.qhurinet.entities.Ruta;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IRutaService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {
    @Autowired
    private IRutaService rS;

    @Autowired
    private IUsuarioService uS;

    @GetMapping("/lista")
    public ResponseEntity<List<RutaDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<RutaDTO> lista = rS.list()
                .stream()
                .map(y -> {
                    RutaDTO dto = m.map(y, RutaDTO.class);
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
    public ResponseEntity<?> registrar(@RequestBody RutaDTO dto) {
        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        Ruta r = m.map(dto, Ruta.class);
        r.setUsuario(usuario.get());

        Ruta cur = rS.insert(r);
        RutaDTO responseDTO = m.map(cur, RutaDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Ruta> ruta = rS.listId(id);

        if (ruta.isPresent()) {
            RutaDTO dto = m.map(ruta.get(), RutaDTO.class);
            dto.setIdUsuario(ruta.get().getUsuario().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ruta no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody RutaDTO dto) {
        Optional<Ruta> existente = rS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ruta no encontrada");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        Ruta r = existente.get();
        r.setUsuario(usuario.get());
        r.setNombre(dto.getNombre());
        r.setPuntosJson(dto.getPuntosJson());
        r.setDistanciaTotalKm(dto.getDistanciaTotalKm());
        r.setTiempoEstimadoMin(dto.getTiempoEstimadoMin());
        r.setFavorita(dto.getFavorita());

        rS.update(r);

        return ResponseEntity.ok("Ruta actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Ruta> ruta = rS.listId(id);

        if (ruta.isPresent()) {
            rS.delete(id);
            return ResponseEntity.ok("Ruta eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ruta no encontrada");
        }
    }
}
