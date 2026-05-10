package pe.edu.upc.qhurinet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.qhurinet.dtos.FaqDTO;
import pe.edu.upc.qhurinet.dtos.SoporteContactoDTO;
import pe.edu.upc.qhurinet.entities.Faq;
import pe.edu.upc.qhurinet.entities.SoporteContacto;
import pe.edu.upc.qhurinet.servicesinterfaces.IFaqService;
import pe.edu.upc.qhurinet.servicesinterfaces.ISoporteContactoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/soporte")
public class SoporteController {
    @Autowired
    private IFaqService fS;

    @Autowired
    private ISoporteContactoService cS;

    @GetMapping("/faqs/lista")
    public ResponseEntity<List<FaqDTO>> listarFaqs(@RequestParam(value = "soloActivas", required = false) Boolean soloActivas) {
        List<FaqDTO> lista = (Boolean.FALSE.equals(soloActivas) ? fS.list() : fS.listActivas())
                .stream()
                .map(this::toFaqDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/faqs/buscar")
    public ResponseEntity<?> buscarFaqs(@RequestParam(value = "categoria", required = false) String categoria,
                                        @RequestParam(value = "q", required = false) String texto) {
        List<FaqDTO> lista = fS.buscarActivas(blankToNull(categoria), blankToNull(texto))
                .stream()
                .map(this::toFaqDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/faqs/nuevo")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrarFaq(@RequestBody FaqDTO dto) {
        String error = validarFaq(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        Faq faq = new Faq();
        faq.setCategoria(dto.getCategoria());
        faq.setPregunta(dto.getPregunta());
        faq.setRespuesta(dto.getRespuesta());
        faq.setActivo(dto.getActivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(toFaqDto(fS.insert(faq)));
    }

    @GetMapping("/faqs/{id}")
    public ResponseEntity<?> buscarFaqPorId(@PathVariable UUID id) {
        Optional<Faq> faq = fS.listId(id);
        if (faq.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FAQ no encontrada");
        }

        return ResponseEntity.ok(toFaqDto(faq.get()));
    }

    @PutMapping("/faqs/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizarFaq(@RequestBody FaqDTO dto) {
        String error = validarFaq(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (dto.getId() == null) {
            return ResponseEntity.badRequest().body("Id de FAQ obligatorio");
        }

        Optional<Faq> faq = fS.listId(dto.getId());
        if (faq.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FAQ no encontrada");
        }

        Faq f = faq.get();
        f.setCategoria(dto.getCategoria());
        f.setPregunta(dto.getPregunta());
        f.setRespuesta(dto.getRespuesta());
        f.setActivo(dto.getActivo());
        fS.update(f);
        return ResponseEntity.ok(toFaqDto(f));
    }

    @DeleteMapping("/faqs/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> eliminarFaq(@PathVariable UUID id) {
        Optional<Faq> faq = fS.listId(id);
        if (faq.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FAQ no encontrada");
        }

        Faq f = faq.get();
        f.setActivo(false);
        fS.update(f);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contacto")
    public ResponseEntity<?> contactoActivo() {
        List<SoporteContactoDTO> lista = cS.listActivos()
                .stream()
                .map(this::toContactoDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay contactos activos");
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/contactos/lista")
    public ResponseEntity<List<SoporteContactoDTO>> listarContactos() {
        List<SoporteContactoDTO> lista = cS.list()
                .stream()
                .map(this::toContactoDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/contactos/nuevo")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrarContacto(@RequestBody SoporteContactoDTO dto) {
        String error = validarContacto(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        SoporteContacto c = new SoporteContacto();
        c.setTipo(dto.getTipo());
        c.setValor(dto.getValor());
        c.setDescripcion(dto.getDescripcion());
        c.setHorario(dto.getHorario());
        c.setActivo(dto.getActivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(toContactoDto(cS.insert(c)));
    }

    @GetMapping("/contactos/{id}")
    public ResponseEntity<?> buscarContactoPorId(@PathVariable UUID id) {
        Optional<SoporteContacto> contacto = cS.listId(id);
        if (contacto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contacto no encontrado");
        }

        return ResponseEntity.ok(toContactoDto(contacto.get()));
    }

    @PutMapping("/contactos/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizarContacto(@RequestBody SoporteContactoDTO dto) {
        String error = validarContacto(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (dto.getId() == null) {
            return ResponseEntity.badRequest().body("Id de contacto obligatorio");
        }

        Optional<SoporteContacto> contacto = cS.listId(dto.getId());
        if (contacto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contacto no encontrado");
        }

        SoporteContacto c = contacto.get();
        c.setTipo(dto.getTipo());
        c.setValor(dto.getValor());
        c.setDescripcion(dto.getDescripcion());
        c.setHorario(dto.getHorario());
        c.setActivo(dto.getActivo());
        cS.update(c);
        return ResponseEntity.ok(toContactoDto(c));
    }

    @DeleteMapping("/contactos/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> eliminarContacto(@PathVariable UUID id) {
        Optional<SoporteContacto> contacto = cS.listId(id);
        if (contacto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contacto no encontrado");
        }

        SoporteContacto c = contacto.get();
        c.setActivo(false);
        cS.update(c);
        return ResponseEntity.noContent().build();
    }

    private FaqDTO toFaqDto(Faq faq) {
        FaqDTO dto = new FaqDTO();
        dto.setId(faq.getId());
        dto.setCategoria(faq.getCategoria());
        dto.setPregunta(faq.getPregunta());
        dto.setRespuesta(faq.getRespuesta());
        dto.setActivo(faq.getActivo());
        dto.setCreatedAt(faq.getCreatedAt());
        return dto;
    }

    private SoporteContactoDTO toContactoDto(SoporteContacto contacto) {
        SoporteContactoDTO dto = new SoporteContactoDTO();
        dto.setId(contacto.getId());
        dto.setTipo(contacto.getTipo());
        dto.setValor(contacto.getValor());
        dto.setDescripcion(contacto.getDescripcion());
        dto.setHorario(contacto.getHorario());
        dto.setActivo(contacto.getActivo());
        dto.setCreatedAt(contacto.getCreatedAt());
        return dto;
    }

    private String validarFaq(FaqDTO dto) {
        if (dto == null || isBlank(dto.getCategoria()) || isBlank(dto.getPregunta()) || isBlank(dto.getRespuesta())) {
            return "Categoria, pregunta y respuesta son obligatorias";
        }
        return null;
    }

    private String validarContacto(SoporteContactoDTO dto) {
        if (dto == null || isBlank(dto.getTipo()) || isBlank(dto.getValor())) {
            return "Tipo y valor son obligatorios";
        }
        return null;
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
