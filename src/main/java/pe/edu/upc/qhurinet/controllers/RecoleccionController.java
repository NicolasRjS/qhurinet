package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.HistorialRecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.PromedioRecolectorDTO;
import pe.edu.upc.qhurinet.dtos.RecoleccionDTO;
import pe.edu.upc.qhurinet.dtos.RecoleccionRangoDTO;
import pe.edu.upc.qhurinet.entities.Publicacion;
import pe.edu.upc.qhurinet.entities.Recoleccion;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IRecoleccionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @GetMapping("/lista")
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
    public ResponseEntity<?> registrar(@RequestBody RecoleccionDTO dto) {
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

        Recoleccion cur = rS.insert(r);
        RecoleccionDTO responseDTO = m.map(cur, RecoleccionDTO.class);
        responseDTO.setIdPublicacion(cur.getPublicacion().getId());
        responseDTO.setIdRecolector(cur.getRecolector().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
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
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Recoleccion> recoleccion = rS.listId(id);

        if (recoleccion.isPresent()) {
            rS.delete(id);
            return ResponseEntity.ok("Recoleccion eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recoleccion no encontrada");
        }
    }

    //Historial completo de un usuario por ID
    @GetMapping("/historial/{idUsuario}")
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
            dto.setId((UUID) fila[0]);
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
        dto.setIdRecolector((UUID) fila[0]);
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
            dto.setId((UUID) fila[0]);
            dto.setFechaProgramada((LocalDateTime) fila[1]);
            dto.setFechaCompletada((LocalDateTime) fila[2]);
            dto.setEstado((String) fila[3]);
            dto.setTituloPublicacion((String) fila[4]);
            dto.setNombreRecolector((String) fila[5]);
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }
}
