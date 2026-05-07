package pe.edu.upc.qhurinet.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.CertificadoDTO;
import pe.edu.upc.qhurinet.entities.Certificado;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.ICertificadoService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {
    @Autowired
    private ICertificadoService cS;

    @Autowired
    private IUsuarioService uS;

    @GetMapping("/lista")
    public ResponseEntity<List<CertificadoDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<CertificadoDTO> lista = cS.list()
                .stream()
                .map(y -> {
                    CertificadoDTO dto = m.map(y, CertificadoDTO.class);
                    dto.setIdUsuario(y.getUsuario().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/por-dificultad")
    public ResponseEntity<?> certificadosPorDificultad(@RequestParam("nivel") String nivel) {
        ModelMapper m = new ModelMapper();

        List<CertificadoDTO> lista = cS.certificadosPorDificultad(nivel)
                .stream()
                .map(y -> {
                    CertificadoDTO dto = m.map(y, CertificadoDTO.class);
                    dto.setIdUsuario(y.getUsuario().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    public ResponseEntity<?> registrar(@RequestBody CertificadoDTO dto) {
        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        Certificado c = m.map(dto, Certificado.class);
        c.setUsuario(usuario.get());

        Certificado cur = cS.insert(c);
        CertificadoDTO responseDTO = m.map(cur, CertificadoDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Certificado> cer = cS.listId(id);

        if (cer.isPresent()) {
            CertificadoDTO dto = m.map(cer.get(), CertificadoDTO.class);
            dto.setIdUsuario(cer.get().getUsuario().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Certificado no encontrado");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody CertificadoDTO dto) {
        Optional<Certificado> existente = cS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Certificado no encontrado");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        Certificado c = existente.get();
        c.setUsuario(usuario.get());
        c.setNombre(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        c.setNivelDificultad(dto.getNivelDificultad());
        c.setPuntosRequeridos(dto.getPuntosRequeridos());
        c.setUrlPdf(dto.getUrlPdf());
        c.setFechaObtencion(dto.getFechaObtencion());

        cS.update(c);

        return ResponseEntity.ok("Certificado actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        Optional<Certificado> certificado = cS.listId(id);

        if (certificado.isPresent()) {
            cS.delete(id);
            return ResponseEntity.ok("Certificado eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Certificado no encontrado");
        }
    }
}
