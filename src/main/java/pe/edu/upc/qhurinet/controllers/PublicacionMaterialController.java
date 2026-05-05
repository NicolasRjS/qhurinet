package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.PublicacionMaterialDTO;
import pe.edu.upc.qhurinet.entities.Material;
import pe.edu.upc.qhurinet.entities.Publicacion;
import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.PublicacionMaterialId;
import pe.edu.upc.qhurinet.servicesinterfaces.IMaterialService;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionMaterialService;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publicaciones-materiales")
public class PublicacionMaterialController {
    @Autowired
    private IPublicacionMaterialService pS;

    @Autowired
    private IPublicacionService publicacionService;

    @Autowired
    private IMaterialService materialService;

    @GetMapping("/lista")
    public ResponseEntity<List<PublicacionMaterialDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<PublicacionMaterialDTO> lista = pS.list()
                .stream()
                .map(y -> {
                    PublicacionMaterialDTO dto = m.map(y, PublicacionMaterialDTO.class);
                    dto.setIdPublicacion(y.getPublicacion().getId());
                    dto.setIdMaterial(y.getMaterial().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    public ResponseEntity<?> registrar(@RequestBody PublicacionMaterialDTO dto) {
        Optional<Publicacion> publicacion = publicacionService.listId(dto.getIdPublicacion());

        if (publicacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }

        Optional<Material> material = materialService.listId(dto.getIdMaterial());

        if (material.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material no encontrado");
        }

        ModelMapper m = new ModelMapper();
        PublicacionMaterial p = m.map(dto, PublicacionMaterial.class);
        PublicacionMaterialId id = new PublicacionMaterialId(dto.getIdPublicacion(), dto.getIdMaterial());
        p.setId(id);
        p.setPublicacion(publicacion.get());
        p.setMaterial(material.get());

        PublicacionMaterial cur = pS.insert(p);
        PublicacionMaterialDTO responseDTO = m.map(cur, PublicacionMaterialDTO.class);
        responseDTO.setIdPublicacion(cur.getPublicacion().getId());
        responseDTO.setIdMaterial(cur.getMaterial().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{idPublicacion}/{idMaterial}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID idPublicacion, @PathVariable int idMaterial) {
        ModelMapper m = new ModelMapper();
        PublicacionMaterialId id = new PublicacionMaterialId(idPublicacion, idMaterial);
        Optional<PublicacionMaterial> pub = pS.listId(id);

        if (pub.isPresent()) {
            PublicacionMaterialDTO dto = m.map(pub.get(), PublicacionMaterialDTO.class);
            dto.setIdPublicacion(pub.get().getPublicacion().getId());
            dto.setIdMaterial(pub.get().getMaterial().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion material no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody PublicacionMaterialDTO dto) {
        PublicacionMaterialId id = new PublicacionMaterialId(dto.getIdPublicacion(), dto.getIdMaterial());
        Optional<PublicacionMaterial> existente = pS.listId(id);

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion material no encontrada");
        }

        Optional<Publicacion> publicacion = publicacionService.listId(dto.getIdPublicacion());

        if (publicacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }

        Optional<Material> material = materialService.listId(dto.getIdMaterial());

        if (material.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material no encontrado");
        }

        PublicacionMaterial p = existente.get();
        p.setId(id);
        p.setPublicacion(publicacion.get());
        p.setMaterial(material.get());
        p.setCantidad(dto.getCantidad());
        p.setUnidad(dto.getUnidad());

        pS.update(p);

        return ResponseEntity.ok("Publicacion material actualizada correctamente");
    }

    @DeleteMapping("/{idPublicacion}/{idMaterial}")
    public ResponseEntity<String> eliminar(@PathVariable UUID idPublicacion, @PathVariable int idMaterial) {
        PublicacionMaterialId id = new PublicacionMaterialId(idPublicacion, idMaterial);
        Optional<PublicacionMaterial> publicacionMaterial = pS.listId(id);

        if (publicacionMaterial.isPresent()) {
            pS.delete(id);
            return ResponseEntity.ok("Publicacion material eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion material no encontrada");
        }
    }
}
