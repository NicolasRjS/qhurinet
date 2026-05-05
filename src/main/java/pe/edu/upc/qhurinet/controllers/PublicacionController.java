package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.PublicacionCategoriaDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionCercanaDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionBusquedaDTO;
import pe.edu.upc.qhurinet.entities.Publicacion;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {
    @Autowired
    private IPublicacionService pS;

    @Autowired
    private IUsuarioService uS;

    @GetMapping("/lista")
    public ResponseEntity<List<PublicacionDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<PublicacionDTO> lista = pS.list()
                .stream()
                .map(y -> {
                    PublicacionDTO dto = m.map(y, PublicacionDTO.class);
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
    public ResponseEntity<?> registrar(@RequestBody PublicacionDTO dto) {
        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        Publicacion p = m.map(dto, Publicacion.class);
        p.setUsuario(usuario.get());

        Publicacion cur = pS.insert(p);
        PublicacionDTO responseDTO = m.map(cur, PublicacionDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Publicacion> pub = pS.listId(id);

        if (pub.isPresent()) {
            PublicacionDTO dto = m.map(pub.get(), PublicacionDTO.class);
            dto.setIdUsuario(pub.get().getUsuario().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody PublicacionDTO dto) {
        Optional<Publicacion> existente = pS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        Publicacion p = existente.get();
        p.setUsuario(usuario.get());
        p.setTitulo(dto.getTitulo());
        p.setObservaciones(dto.getObservaciones());
        p.setEstado(dto.getEstado());
        p.setLatitud(dto.getLatitud());
        p.setLongitud(dto.getLongitud());
        p.setDireccionReferencia(dto.getDireccionReferencia());
        p.setFechaDisponibilidad(dto.getFechaDisponibilidad());
        p.setImagenesJson(dto.getImagenesJson());

        pS.update(p);

        return ResponseEntity.ok("Publicacion actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Publicacion> publicacion = pS.listId(id);

        if (publicacion.isPresent()) {
            pS.delete(id);
            return ResponseEntity.ok("Publicacion eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPublicaciones(@RequestParam("q") String texto) {
        List<Object[]> lista = pS.buscarPublicacionesPorTexto(texto);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay registros");
        }

        List<PublicacionBusquedaDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            PublicacionBusquedaDTO dto = new PublicacionBusquedaDTO();
            dto.setId((UUID) fila[0]);
            dto.setTitulo((String) fila[1]);
            dto.setDireccionReferencia((String) fila[2]);
            dto.setLatitud(((Number) fila[3]).doubleValue());
            dto.setLongitud(((Number) fila[4]).doubleValue());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }
    @GetMapping("/cercanas")
    public ResponseEntity<?> publicacionesCercanas(@RequestParam("lat") Double lat,
                                                   @RequestParam("lng") Double lng,
                                                   @RequestParam("radio") Double radio) {
        List<Object[]> lista = pS.publicacionesCercanas(lat, lng, radio);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<PublicacionCercanaDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            PublicacionCercanaDTO dto = new PublicacionCercanaDTO();
            dto.setId((UUID) fila[0]);
            dto.setTitulo((String) fila[1]);
            dto.setLatitud(((Number) fila[2]).doubleValue());
            dto.setLongitud(((Number) fila[3]).doubleValue());
            dto.setDistanciaKm(((Number) fila[4]).doubleValue());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }
    @GetMapping("/por-categoria/{categoria}")
    public ResponseEntity<?> publicacionesPorCategoria(@PathVariable String categoria) {
        List<Object[]> lista = pS.publicacionesPorCategoriaMaterial(categoria);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<PublicacionCategoriaDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            PublicacionCategoriaDTO dto = new PublicacionCategoriaDTO();
            dto.setIdPublicacion((UUID) fila[0]);
            dto.setTitulo((String) fila[1]);
            dto.setLatitud(((Number) fila[2]).doubleValue());
            dto.setLongitud(((Number) fila[3]).doubleValue());
            dto.setNombreMaterial((String) fila[4]);
            dto.setCantidad(((Number) fila[5]).doubleValue());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }
}
