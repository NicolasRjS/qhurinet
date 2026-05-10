package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.ClasificacionMaterialDTO;
import pe.edu.upc.qhurinet.dtos.ClasificarMaterialRequestDTO;
import pe.edu.upc.qhurinet.dtos.MaterialDTO;
import pe.edu.upc.qhurinet.dtos.MaterialSugerenciaDTO;
import pe.edu.upc.qhurinet.dtos.MaterialTopDTO;
import pe.edu.upc.qhurinet.entities.Material;
import pe.edu.upc.qhurinet.servicesinterfaces.IClasificacionMaterialService;
import pe.edu.upc.qhurinet.servicesinterfaces.IMaterialService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/materiales")
public class MaterialController {
    @Autowired
    private IMaterialService mS;

    @Autowired
    private IClasificacionMaterialService clasificacionMaterialService;

    @GetMapping("/lista")
    public ResponseEntity<List<MaterialDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<MaterialDTO> lista = mS.list()
                .stream()
                .map(y -> m.map(y, MaterialDTO.class))
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody MaterialDTO dto) {
        ModelMapper m = new ModelMapper();
        Material mat = m.map(dto, Material.class);
        Material cur = mS.insert(mat);
        MaterialDTO responseDTO = m.map(cur, MaterialDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Material> mat = mS.listId(id);

        if (mat.isPresent()) {
            MaterialDTO dto = m.map(mat.get(), MaterialDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody MaterialDTO dto) {
        Optional<Material> existente = mS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material no encontrado");
        }

        Material m = existente.get();
        m.setNombre(dto.getNombre());
        m.setCategoria(dto.getCategoria());
        m.setDescripcion(dto.getDescripcion());
        m.setPuntosPorKg(dto.getPuntosPorKg());

        mS.update(m);

        return ResponseEntity.ok("Material actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Material> material = mS.listId(id);

        if (material.isPresent()) {
            mS.delete(id);
            return ResponseEntity.ok("Material eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material no encontrado");
        }
    }

    @GetMapping("/top5")
    public ResponseEntity<?> listarTop5Materiales() {
        List<Object[]> lista = mS.top5MaterialesMasReciclados();

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay registros");
        }

        List<MaterialTopDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            MaterialTopDTO dto = new MaterialTopDTO();
            dto.setNombreMaterial((String) fila[0]);
            dto.setCategoria((String) fila[1]);
            dto.setTotalKg(((Number) fila[2]).doubleValue());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/sugerencia")
    public ResponseEntity<?> sugerirCategoria(@RequestParam("texto") String texto) {
        List<Object[]> lista = mS.sugerirCategoriaMaterial(texto);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material no reconocido. Seleccione una categoria manualmente");
        }

        List<MaterialSugerenciaDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            MaterialSugerenciaDTO dto = new MaterialSugerenciaDTO();
            dto.setIdMaterial(((Number) fila[0]).intValue());
            dto.setNombreMaterial((String) fila[1]);
            dto.setCategoria((String) fila[2]);
            dto.setDescripcion((String) fila[3]);
            dto.setPuntosPorKg(toBigDecimal(fila[4]));
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/clasificar")
    public ResponseEntity<?> clasificarMaterial(@RequestBody ClasificarMaterialRequestDTO request) {
        String texto = textoClasificacion(request);
        if (texto == null || texto.isBlank()) {
            return ResponseEntity.badRequest().body("Texto, titulo u observaciones son obligatorios");
        }

        List<ClasificacionMaterialDTO> respuesta = clasificacionMaterialService.clasificar(texto);
        if (respuesta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material no reconocido. Seleccione una categoria manualmente");
        }
        return ResponseEntity.ok(respuesta);
    }

    private String textoClasificacion(ClasificarMaterialRequestDTO request) {
        if (request == null) {
            return null;
        }
        if (request.getTexto() != null && !request.getTexto().isBlank()) {
            return request.getTexto();
        }
        return ((request.getTitulo() == null ? "" : request.getTitulo()) + " "
                + (request.getObservaciones() == null ? "" : request.getObservaciones())).trim();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return new BigDecimal(value.toString());
    }
}
