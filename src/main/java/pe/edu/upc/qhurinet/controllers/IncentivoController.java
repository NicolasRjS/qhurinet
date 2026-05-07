package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.IncentivoDisponibleDTO;
import pe.edu.upc.qhurinet.dtos.IncentivoDTO;
import pe.edu.upc.qhurinet.entities.Incentivo;
import pe.edu.upc.qhurinet.servicesinterfaces.IIncentivoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/incentivos")
public class IncentivoController {
    @Autowired
    private IIncentivoService iS;

    @GetMapping("/lista")
    public ResponseEntity<List<IncentivoDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<IncentivoDTO> lista = iS.list()
                .stream()
                .map(y -> m.map(y, IncentivoDTO.class))
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    public ResponseEntity<?> registrar(@RequestBody IncentivoDTO dto) {
        ModelMapper m = new ModelMapper();
        Incentivo i = m.map(dto, Incentivo.class);
        Incentivo cur = iS.insert(i);
        IncentivoDTO responseDTO = m.map(cur, IncentivoDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Incentivo> incentivo = iS.listId(id);

        if (incentivo.isPresent()) {
            IncentivoDTO dto = m.map(incentivo.get(), IncentivoDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incentivo no encontrado");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody IncentivoDTO dto) {
        Optional<Incentivo> existente = iS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incentivo no encontrado");
        }

        Incentivo i = existente.get();
        i.setTipo(dto.getTipo());
        i.setNombre(dto.getNombre());
        i.setDescripcion(dto.getDescripcion());
        i.setCostoPuntos(dto.getCostoPuntos());
        i.setMetaCantidad(dto.getMetaCantidad());
        i.setMetaUnidad(dto.getMetaUnidad());
        i.setFechaInicio(dto.getFechaInicio());
        i.setFechaFin(dto.getFechaFin());
        i.setStock(dto.getStock());
        i.setActivo(dto.getActivo());

        iS.update(i);

        return ResponseEntity.ok("Incentivo actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Incentivo> incentivo = iS.listId(id);

        if (incentivo.isPresent()) {
            iS.delete(id);
            return ResponseEntity.ok("Incentivo eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incentivo no encontrado");
        }
    }

    @GetMapping("/disponibles/{idUsuario}")
    public ResponseEntity<?> incentivosDisponibles(@PathVariable UUID idUsuario) {
        List<Object[]> lista = iS.incentivosDisponiblesUsuario(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<IncentivoDisponibleDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            IncentivoDisponibleDTO dto = new IncentivoDisponibleDTO();
            dto.setIdIncentivo(toUuid(fila[0]));
            dto.setTipo((String) fila[1]);
            dto.setNombre((String) fila[2]);
            dto.setDescripcion((String) fila[3]);
            dto.setCostoPuntos(toInteger(fila[4]));
            dto.setStock(toInteger(fila[5]));
            dto.setActivo(toBoolean(fila[6]));
            dto.setPuntosUsuario(toInteger(fila[7]));
            dto.setPuntosSuficientes(toBoolean(fila[8]));
            dto.setYaRegistrado(toBoolean(fila[9]));
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    private UUID toUuid(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(value.toString());
    }

    private Integer toInteger(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(value.toString());
    }
}
