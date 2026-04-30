package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.DocumentoVerificacionDTO;
import pe.edu.upc.qhurinet.entities.DocumentoVerificacion;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IDocumentoVerificacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

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

    @GetMapping("/lista")
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

    @DeleteMapping("/{id}")
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
