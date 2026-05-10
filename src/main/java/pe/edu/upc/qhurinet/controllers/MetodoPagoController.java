package pe.edu.upc.qhurinet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.qhurinet.dtos.MetodoPagoDTO;
import pe.edu.upc.qhurinet.entities.MetodoPago;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IMetodoPagoService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {
    @Autowired
    private IMetodoPagoService mS;

    @Autowired
    private IUsuarioService uS;

    @GetMapping("/lista")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<MetodoPagoDTO>> listar() {
        List<MetodoPagoDTO> lista = mS.list()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> listarPorUsuario(@PathVariable UUID idUsuario,
                                              @RequestParam(value = "soloActivos", required = false) Boolean soloActivos) {
        List<MetodoPagoDTO> lista = (Boolean.FALSE.equals(soloActivos)
                ? mS.listByUsuario(idUsuario)
                : mS.listActivosByUsuario(idUsuario))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        return ResponseEntity.ok(lista);
    }

    @Transactional
    @PostMapping("/nuevo")
    @PreAuthorize("@securityPermissionService.canCreateMetodoPago(#dto)")
    public ResponseEntity<?> registrar(@RequestBody MetodoPagoDTO dto) {
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        if (Boolean.TRUE.equals(dto.getPrincipal())) {
            quitarPrincipal(dto.getIdUsuario());
        }

        MetodoPago metodo = new MetodoPago();
        metodo.setUsuario(usuario.get());
        metodo.setTipo(dto.getTipo());
        metodo.setAlias(dto.getAlias());
        metodo.setTitular(dto.getTitular());
        metodo.setDetalleEnmascarado(dto.getDetalleEnmascarado());
        metodo.setPrincipal(dto.getPrincipal());
        metodo.setActivo(dto.getActivo());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(mS.insert(metodo)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isMetodoPagoOwner(#id)")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        Optional<MetodoPago> metodo = mS.listId(id);
        if (metodo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Metodo de pago no encontrado");
        }

        return ResponseEntity.ok(toDto(metodo.get()));
    }

    @Transactional
    @PutMapping("/actualiza")
    @PreAuthorize("@securityPermissionService.isMetodoPagoOwner(#dto)")
    public ResponseEntity<?> actualizar(@RequestBody MetodoPagoDTO dto) {
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (dto.getId() == null) {
            return ResponseEntity.badRequest().body("Id de metodo de pago obligatorio");
        }

        Optional<MetodoPago> metodo = mS.listId(dto.getId());
        if (metodo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Metodo de pago no encontrado");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        if (Boolean.TRUE.equals(dto.getPrincipal())) {
            quitarPrincipal(dto.getIdUsuario());
        }

        MetodoPago m = metodo.get();
        m.setUsuario(usuario.get());
        m.setTipo(dto.getTipo());
        m.setAlias(dto.getAlias());
        m.setTitular(dto.getTitular());
        m.setDetalleEnmascarado(dto.getDetalleEnmascarado());
        m.setPrincipal(dto.getPrincipal());
        m.setActivo(dto.getActivo());
        mS.update(m);
        return ResponseEntity.ok(toDto(m));
    }

    @Transactional
    @PatchMapping("/{id}/principal")
    @PreAuthorize("@securityPermissionService.isMetodoPagoOwner(#id)")
    public ResponseEntity<?> marcarPrincipal(@PathVariable UUID id) {
        Optional<MetodoPago> metodo = mS.listId(id);
        if (metodo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Metodo de pago no encontrado");
        }

        MetodoPago m = metodo.get();
        quitarPrincipal(m.getUsuario().getId());
        m.setPrincipal(true);
        m.setActivo(true);
        mS.update(m);
        return ResponseEntity.ok(toDto(m));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isMetodoPagoOwner(#id)")
    public ResponseEntity<?> eliminar(@PathVariable UUID id) {
        Optional<MetodoPago> metodo = mS.listId(id);
        if (metodo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Metodo de pago no encontrado");
        }

        MetodoPago m = metodo.get();
        m.setActivo(false);
        m.setPrincipal(false);
        mS.update(m);
        return ResponseEntity.noContent().build();
    }

    private MetodoPagoDTO toDto(MetodoPago metodo) {
        MetodoPagoDTO dto = new MetodoPagoDTO();
        dto.setId(metodo.getId());
        dto.setIdUsuario(metodo.getUsuario().getId());
        dto.setTipo(metodo.getTipo());
        dto.setAlias(metodo.getAlias());
        dto.setTitular(metodo.getTitular());
        dto.setDetalleEnmascarado(metodo.getDetalleEnmascarado());
        dto.setPrincipal(metodo.getPrincipal());
        dto.setActivo(metodo.getActivo());
        dto.setCreatedAt(metodo.getCreatedAt());
        return dto;
    }

    private String validar(MetodoPagoDTO dto) {
        if (dto == null || dto.getIdUsuario() == null || isBlank(dto.getTipo()) || isBlank(dto.getDetalleEnmascarado())) {
            return "Usuario, tipo y detalle enmascarado son obligatorios";
        }
        return null;
    }

    private void quitarPrincipal(UUID idUsuario) {
        for (MetodoPago metodo : mS.listActivosByUsuario(idUsuario)) {
            if (Boolean.TRUE.equals(metodo.getPrincipal())) {
                metodo.setPrincipal(false);
                mS.update(metodo);
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
