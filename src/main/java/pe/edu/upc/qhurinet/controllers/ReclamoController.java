package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upc.qhurinet.dtos.ReclamoDTO;
import pe.edu.upc.qhurinet.entities.Reclamo;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IArchivoStorageService;
import pe.edu.upc.qhurinet.servicesinterfaces.IReclamoService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reclamos")
public class ReclamoController {
    @Autowired
    private IReclamoService rS;

    @Autowired
    private IUsuarioService uS;

    @Autowired
    private IArchivoStorageService archivoStorageService;

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReclamoDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<ReclamoDTO> lista = rS.list()
                .stream()
                .map(y -> {
                    ReclamoDTO dto = m.map(y, ReclamoDTO.class);
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
    @PreAuthorize("@securityPermissionService.canCreateReclamo(#dto)")
    public ResponseEntity<?> registrar(@RequestBody ReclamoDTO dto) {
        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        Reclamo r = m.map(dto, Reclamo.class);
        r.setUsuario(usuario.get());

        Reclamo cur = rS.insert(r);
        ReclamoDTO responseDTO = m.map(cur, ReclamoDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isReclamoOwner(#id)")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Reclamo> reclamo = rS.listId(id);

        if (reclamo.isPresent()) {
            ReclamoDTO dto = m.map(reclamo.get(), ReclamoDTO.class);
            dto.setIdUsuario(reclamo.get().getUsuario().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reclamo no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("@securityPermissionService.isReclamoOwner(#dto.id)")
    public ResponseEntity<String> actualizar(@RequestBody ReclamoDTO dto) {
        Optional<Reclamo> existente = rS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reclamo no encontrado");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        Reclamo r = existente.get();
        r.setUsuario(usuario.get());
        r.setAsunto(dto.getAsunto());
        r.setDescripcion(dto.getDescripcion());
        r.setEvidenciaUrl(dto.getEvidenciaUrl());
        r.setEstado(dto.getEstado());
        r.setRespuesta(dto.getRespuesta());

        rS.update(r);

        return ResponseEntity.ok("Reclamo actualizado correctamente");
    }

    @PostMapping(value = "/{id}/evidencia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@securityPermissionService.isReclamoOwner(#id)")
    public ResponseEntity<?> subirEvidencia(@PathVariable UUID id,
                                            @RequestParam("file") MultipartFile file) {
        Optional<Reclamo> reclamo = rS.listId(id);
        if (reclamo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reclamo no encontrado");
        }

        try {
            var archivo = archivoStorageService.guardarImagen(file, "reclamos");
            Reclamo r = reclamo.get();
            r.setEvidenciaUrl(archivo.getUrl());
            rS.update(r);
            return ResponseEntity.status(HttpStatus.CREATED).body(archivo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar el archivo");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isReclamoOwner(#id)")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Reclamo> reclamo = rS.listId(id);

        if (reclamo.isPresent()) {
            rS.delete(id);
            return ResponseEntity.ok("Reclamo eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reclamo no encontrado");
        }
    }
}
