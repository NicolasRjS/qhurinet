package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upc.qhurinet.dtos.ArchivoUrlDTO;
import pe.edu.upc.qhurinet.dtos.DocumentoVerificacionDTO;
import pe.edu.upc.qhurinet.entities.DocumentoVerificacion;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IArchivoStorageService;
import pe.edu.upc.qhurinet.servicesinterfaces.IDocumentoVerificacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documentos-verificacion")
public class DocumentoVerificacionController {
    @Autowired
    private IDocumentoVerificacionService dS;

    @Autowired
    private IUsuarioService uS;

    @Autowired
    private IArchivoStorageService archivoStorageService;

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<DocumentoVerificacionDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<DocumentoVerificacionDTO> lista = dS.list()
                .stream()
                .map(y -> {
                    DocumentoVerificacionDTO dto = m.map(y, DocumentoVerificacionDTO.class);
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
    @PreAuthorize("@securityPermissionService.canCreateForUser(#dto.idUsuario)")
    public ResponseEntity<?> registrar(@RequestBody DocumentoVerificacionDTO dto) {
        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        DocumentoVerificacion d = m.map(dto, DocumentoVerificacion.class);
        d.setUsuario(usuario.get());

        DocumentoVerificacion cur = dS.insert(d);
        DocumentoVerificacionDTO responseDTO = m.map(cur, DocumentoVerificacionDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isDocumentoOwner(#id)")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<DocumentoVerificacion> doc = dS.listId(id);

        if (doc.isPresent()) {
            DocumentoVerificacionDTO dto = m.map(doc.get(), DocumentoVerificacionDTO.class);
            dto.setIdUsuario(doc.get().getUsuario().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documento de verificacion no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("@securityPermissionService.isDocumentoOwner(#dto.id)")
    public ResponseEntity<String> actualizar(@RequestBody DocumentoVerificacionDTO dto) {
        Optional<DocumentoVerificacion> existente = dS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documento de verificacion no encontrado");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        DocumentoVerificacion d = existente.get();
        d.setUsuario(usuario.get());
        d.setTipo(dto.getTipo());
        d.setUrlArchivo(dto.getUrlArchivo());
        d.setEstado(dto.getEstado());
        d.setMotivoRechazo(dto.getMotivoRechazo());
        d.setReviewedAt(dto.getReviewedAt());

        dS.update(d);

        return ResponseEntity.ok("Documento de verificacion actualizado correctamente");
    }

    @PatchMapping("/{id}/archivo-url")
    @PreAuthorize("@securityPermissionService.isDocumentoOwner(#id)")
    public ResponseEntity<?> actualizarArchivoUrl(@PathVariable UUID id,
                                                  @RequestBody ArchivoUrlDTO dto) {
        Optional<DocumentoVerificacion> existente = dS.listId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documento de verificacion no encontrado");
        }
        if (dto == null || dto.getUrl() == null || dto.getUrl().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("URL de archivo obligatoria");
        }

        DocumentoVerificacion d = existente.get();
        d.setUrlArchivo(dto.getUrl());
        dS.update(d);

        ModelMapper m = new ModelMapper();
        DocumentoVerificacionDTO responseDTO = m.map(d, DocumentoVerificacionDTO.class);
        responseDTO.setIdUsuario(d.getUsuario().getId());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping(value = "/{id}/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@securityPermissionService.isDocumentoOwner(#id)")
    public ResponseEntity<?> subirArchivo(@PathVariable UUID id,
                                          @RequestParam("file") MultipartFile file) {
        Optional<DocumentoVerificacion> existente = dS.listId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documento de verificacion no encontrado");
        }

        try {
            var archivo = archivoStorageService.guardarDocumento(file, "documentos-verificacion");
            DocumentoVerificacion d = existente.get();
            d.setUrlArchivo(archivo.getUrl());
            dS.update(d);
            return ResponseEntity.status(HttpStatus.CREATED).body(archivo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar el archivo");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isDocumentoOwner(#id)")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<DocumentoVerificacion> documento = dS.listId(id);

        if (documento.isPresent()) {
            dS.delete(id);
            return ResponseEntity.ok("Documento de verificacion eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documento de verificacion no encontrado");
        }
    }
}
