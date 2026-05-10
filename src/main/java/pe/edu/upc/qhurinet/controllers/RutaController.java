package pe.edu.upc.qhurinet.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.RutaDTO;
import pe.edu.upc.qhurinet.dtos.RutaOptimaRequestDTO;
import pe.edu.upc.qhurinet.dtos.RutaOptimaResponseDTO;
import pe.edu.upc.qhurinet.entities.Ruta;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IRutaOptimaService;
import pe.edu.upc.qhurinet.servicesinterfaces.IRutaService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/rutas", "/rutas"})
public class RutaController {
    @Autowired
    private IRutaService rS;

    @Autowired
    private IUsuarioService uS;

    @Autowired
    private IRutaOptimaService rutaOptimaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("@securityPermissionService.canCreateRuta(#dto)")
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
    @PreAuthorize("@securityPermissionService.isRutaOwner(#id)")
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
    @PreAuthorize("@securityPermissionService.isRutaOwner(#dto)")
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
    @PreAuthorize("@securityPermissionService.isRutaOwner(#id)")
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

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> rutasPorUsuario(@PathVariable UUID idUsuario) {
        List<RutaDTO> lista = rS.listByUsuario(idUsuario)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{idUsuario}/favoritas")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> rutasFavoritasPorUsuario(@PathVariable UUID idUsuario) {
        List<RutaDTO> lista = rS.listFavoritasByUsuario(idUsuario)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        return ResponseEntity.ok(lista);
    }

    @PatchMapping("/{id}/favorita")
    @PreAuthorize("@securityPermissionService.isRutaOwner(#id)")
    public ResponseEntity<?> cambiarFavorita(@PathVariable UUID id,
                                             @RequestParam(value = "favorita", required = false) Boolean favorita) {
        Optional<Ruta> ruta = rS.listId(id);
        if (ruta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ruta no encontrada");
        }

        Ruta r = ruta.get();
        boolean nuevoValor = favorita != null ? favorita : !Boolean.TRUE.equals(r.getFavorita());
        r.setFavorita(nuevoValor);
        rS.update(r);
        return ResponseEntity.ok(toDto(r));
    }

    @PostMapping({"/optima", "/calcular"})
    @PreAuthorize("#dto == null or #dto.idUsuario == null or @securityPermissionService.canCreateForUser(#dto.idUsuario)")
    public ResponseEntity<?> calcularRutaOptima(@RequestBody RutaOptimaRequestDTO dto) {
        try {
            RutaOptimaResponseDTO response = rutaOptimaService.calcular(dto);
            String puntosJson = objectMapper.writeValueAsString(response.getPuntosOrdenados());
            response.setPuntosJson(puntosJson);

            if (Boolean.TRUE.equals(dto.getGuardar())) {
                if (dto.getIdUsuario() == null) {
                    return ResponseEntity.badRequest().body("idUsuario es obligatorio para guardar la ruta");
                }
                Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());
                if (usuario.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
                }

                Ruta ruta = new Ruta();
                ruta.setUsuario(usuario.get());
                ruta.setNombre(dto.getNombre() == null || dto.getNombre().isBlank() ? "Ruta optima" : dto.getNombre());
                ruta.setPuntosJson(puntosJson);
                ruta.setDistanciaTotalKm(BigDecimal.valueOf(response.getDistanciaTotalKm()).setScale(2, RoundingMode.HALF_UP));
                ruta.setTiempoEstimadoMin(response.getTiempoEstimadoMin());
                ruta.setFavorita(false);
                Ruta guardada = rS.insert(ruta);
                response.setIdRuta(guardada.getId());
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo serializar la ruta");
        }
    }

    private RutaDTO toDto(Ruta ruta) {
        ModelMapper m = new ModelMapper();
        RutaDTO dto = m.map(ruta, RutaDTO.class);
        dto.setIdUsuario(ruta.getUsuario().getId());
        return dto;
    }
}
