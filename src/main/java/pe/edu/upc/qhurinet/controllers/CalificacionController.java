package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.CalificacionDTO;
import pe.edu.upc.qhurinet.entities.Calificacion;
import pe.edu.upc.qhurinet.entities.Recoleccion;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.ICalificacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IRecoleccionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {
    @Autowired
    private ICalificacionService cS;

    @Autowired
    private IRecoleccionService recoleccionService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/lista")
    public ResponseEntity<List<CalificacionDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<CalificacionDTO> lista = cS.list()
                .stream()
                .map(y -> {
                    CalificacionDTO dto = m.map(y, CalificacionDTO.class);
                    dto.setIdRecoleccion(y.getRecoleccion().getId());
                    dto.setIdAutor(y.getAutor().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    public ResponseEntity<?> registrar(@RequestBody CalificacionDTO dto) {
        Optional<Recoleccion> recoleccion = recoleccionService.listId(dto.getIdRecoleccion());

        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recoleccion no encontrada");
        }

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdAutor());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Autor no encontrado");
        }

        ModelMapper m = new ModelMapper();
        Calificacion c = m.map(dto, Calificacion.class);
        c.setRecoleccion(recoleccion.get());
        c.setAutor(usuario.get());

        Calificacion cur = cS.insert(c);
        CalificacionDTO responseDTO = m.map(cur, CalificacionDTO.class);
        responseDTO.setIdRecoleccion(cur.getRecoleccion().getId());
        responseDTO.setIdAutor(cur.getAutor().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Calificacion> cal = cS.listId(id);

        if (cal.isPresent()) {
            CalificacionDTO dto = m.map(cal.get(), CalificacionDTO.class);
            dto.setIdRecoleccion(cal.get().getRecoleccion().getId());
            dto.setIdAutor(cal.get().getAutor().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Calificacion no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody CalificacionDTO dto) {
        Optional<Calificacion> existente = cS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Calificacion no encontrada");
        }

        Optional<Recoleccion> recoleccion = recoleccionService.listId(dto.getIdRecoleccion());

        if (recoleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recoleccion no encontrada");
        }

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdAutor());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Autor no encontrado");
        }

        Calificacion c = existente.get();
        c.setRecoleccion(recoleccion.get());
        c.setAutor(usuario.get());
        c.setPuntuacion(dto.getPuntuacion());
        c.setComentario(dto.getComentario());

        cS.update(c);

        return ResponseEntity.ok("Calificacion actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Calificacion> calificacion = cS.listId(id);

        if (calificacion.isPresent()) {
            cS.delete(id);
            return ResponseEntity.ok("Calificacion eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Calificacion no encontrada");
        }
    }
}
