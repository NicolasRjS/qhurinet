package pe.edu.upc.qhurinet.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.qhurinet.dtos.ArchivoUrlDTO;
import pe.edu.upc.qhurinet.dtos.ClasificacionMaterialDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionCategoriaDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionCercanaDTO;
import pe.edu.upc.qhurinet.dtos.HistorialMaterialUsuarioDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionBusquedaDTO;
import pe.edu.upc.qhurinet.dtos.PublicacionMapaDTO;
import pe.edu.upc.qhurinet.entities.MapaCache;
import pe.edu.upc.qhurinet.entities.Publicacion;
import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.IArchivoStorageService;
import pe.edu.upc.qhurinet.servicesinterfaces.IClasificacionMaterialService;
import pe.edu.upc.qhurinet.servicesinterfaces.IMapaCacheService;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionMaterialService;
import pe.edu.upc.qhurinet.servicesinterfaces.IPublicacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {
    @Autowired
    private IPublicacionService pS;

    @Autowired
    private IUsuarioService uS;

    @Autowired
    private IMapaCacheService mapaCacheService;

    @Autowired
    private IArchivoStorageService archivoStorageService;

    @Autowired
    private IClasificacionMaterialService clasificacionMaterialService;

    @Autowired
    private IPublicacionMaterialService publicacionMaterialService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @GetMapping("/lista")
    public ResponseEntity<List<PublicacionDTO>> listar() {
        ModelMapper m = new ModelMapper();

        List<PublicacionDTO> lista = pS.list()
                .stream()
                .map(y -> {
                    PublicacionDTO dto = m.map(y, PublicacionDTO.class);
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
    @PreAuthorize("@securityPermissionService.canCreatePublicacion(#dto)")
    public ResponseEntity<?> registrar(@RequestBody PublicacionDTO dto) {
        String error = validarPublicacion(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        Publicacion p = m.map(dto, Publicacion.class);
        p.setUsuario(usuario.get());

        Publicacion cur = pS.insert(p);
        PublicacionDTO responseDTO = m.map(cur, PublicacionDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        ModelMapper m = new ModelMapper();
        Optional<Publicacion> pub = pS.listId(id);

        if (pub.isPresent()) {
            PublicacionDTO dto = m.map(pub.get(), PublicacionDTO.class);
            dto.setIdUsuario(pub.get().getUsuario().getId());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }
    }

    @GetMapping("/{id}/detalle-mapa")
    public ResponseEntity<?> detalleMapa(@PathVariable UUID id) {
        Optional<Publicacion> pub = pS.listId(id);
        if (pub.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }

        Publicacion publicacion = pub.get();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idPublicacion", publicacion.getId());
        response.put("titulo", publicacion.getTitulo());
        response.put("estado", publicacion.getEstado());
        response.put("direccionReferencia", publicacion.getDireccionReferencia());
        response.put("latitud", publicacion.getLatitud());
        response.put("longitud", publicacion.getLongitud());
        response.put("fechaDisponibilidad", publicacion.getFechaDisponibilidad());
        response.put("observaciones", publicacion.getObservaciones());
        response.put("imagenesJson", publicacion.getImagenesJson());

        Usuario usuario = publicacion.getUsuario();
        Map<String, Object> usuarioMap = new LinkedHashMap<>();
        usuarioMap.put("idUsuario", usuario.getId());
        usuarioMap.put("nombre", usuario.getNombre());
        usuarioMap.put("tipoCuenta", usuario.getTipoCuenta());
        usuarioMap.put("fotoUrl", usuario.getFotoUrl());
        usuarioMap.put("verificado", usuario.getVerificado());
        response.put("usuario", usuarioMap);

        List<Map<String, Object>> materiales = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        for (PublicacionMaterial pm : publicacionMaterialService.listByPublicacion(id)) {
            Map<String, Object> material = new LinkedHashMap<>();
            material.put("idMaterial", pm.getMaterial().getId());
            material.put("nombre", pm.getMaterial().getNombre());
            material.put("categoria", pm.getMaterial().getCategoria());
            material.put("cantidad", pm.getCantidad());
            material.put("unidad", pm.getUnidad());
            material.put("puntosPorKg", pm.getMaterial().getPuntosPorKg());
            materiales.add(material);
            if (!etiquetas.contains(pm.getMaterial().getCategoria())) {
                etiquetas.add(pm.getMaterial().getCategoria());
            }
        }
        response.put("materiales", materiales);
        response.put("etiquetas", etiquetas);

        List<Object[]> perfil = uS.perfilRecolector(usuario.getId());
        Map<String, Object> calificacion = new LinkedHashMap<>();
        if (!perfil.isEmpty()) {
            Object[] fila = perfil.get(0);
            calificacion.put("puntuacionPromedio", toDouble(fila[10]));
            calificacion.put("totalValoraciones", toLong(fila[11]));
        } else {
            calificacion.put("puntuacionPromedio", 0.0);
            calificacion.put("totalValoraciones", 0L);
        }
        response.put("calificacion", calificacion);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/actualiza")
    @PreAuthorize("@securityPermissionService.isPublicacionOwner(#dto)")
    public ResponseEntity<String> actualizar(@RequestBody PublicacionDTO dto) {
        String error = validarPublicacion(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        Optional<Publicacion> existente = pS.listId(dto.getId());

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }

        Optional<Usuario> usuario = uS.listId(dto.getIdUsuario());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        Publicacion p = existente.get();
        p.setUsuario(usuario.get());
        p.setTitulo(dto.getTitulo());
        p.setObservaciones(dto.getObservaciones());
        p.setEstado(dto.getEstado());
        p.setLatitud(dto.getLatitud());
        p.setLongitud(dto.getLongitud());
        p.setDireccionReferencia(dto.getDireccionReferencia());
        p.setFechaDisponibilidad(dto.getFechaDisponibilidad());
        p.setImagenesJson(dto.getImagenesJson());

        pS.update(p);

        return ResponseEntity.ok("Publicacion actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityPermissionService.isPublicacionOwner(#id)")
    public ResponseEntity<?> eliminar(@PathVariable UUID id) {
        Optional<Publicacion> publicacion = pS.listId(id);

        if (publicacion.isPresent()) {
            Publicacion p = publicacion.get();
            p.setEstado("cancelada");
            pS.update(p);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publicacion no encontrada");
        }
    }

    @PatchMapping("/{id}/evidencia-url")
    @PreAuthorize("@securityPermissionService.isPublicacionOwner(#id)")
    public ResponseEntity<?> actualizarEvidenciaUrl(@PathVariable UUID id,
                                                    @RequestBody ArchivoUrlDTO dto) {
        Optional<Publicacion> publicacion = pS.listId(id);
        if (publicacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicacion no encontrada");
        }
        if (dto == null || isBlank(dto.getUrl())) {
            return ResponseEntity.badRequest().body("URL de evidencia obligatoria");
        }

        Publicacion p = publicacion.get();
        p.setImagenesJson(dto.getUrl());
        pS.update(p);
        return ResponseEntity.ok(toPublicacionDto(p));
    }

    @PostMapping(value = "/{id}/evidencia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@securityPermissionService.isPublicacionOwner(#id)")
    public ResponseEntity<?> subirEvidencia(@PathVariable UUID id,
                                            @RequestParam("file") MultipartFile file) {
        Optional<Publicacion> publicacion = pS.listId(id);
        if (publicacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicacion no encontrada");
        }

        try {
            var archivo = archivoStorageService.guardarImagen(file, "publicaciones");
            Publicacion p = publicacion.get();
            p.setImagenesJson(archivo.getUrl());
            pS.update(p);
            return ResponseEntity.status(HttpStatus.CREATED).body(archivo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar el archivo");
        }
    }

    @PostMapping("/{id}/clasificar-materiales")
    @PreAuthorize("@securityPermissionService.isPublicacionOwner(#id)")
    public ResponseEntity<?> clasificarMateriales(@PathVariable UUID id) {
        Optional<Publicacion> publicacion = pS.listId(id);
        if (publicacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicacion no encontrada");
        }

        Publicacion p = publicacion.get();
        String texto = ((p.getTitulo() == null ? "" : p.getTitulo()) + " "
                + (p.getObservaciones() == null ? "" : p.getObservaciones())).trim();
        List<ClasificacionMaterialDTO> respuesta = clasificacionMaterialService.clasificar(texto);
        if (respuesta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material no reconocido. Seleccione una categoria manualmente");
        }
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPublicaciones(@RequestParam("q") String texto) {
        List<Object[]> lista = pS.buscarPublicacionesPorTexto(texto);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay registros");
        }

        List<PublicacionBusquedaDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            PublicacionBusquedaDTO dto = new PublicacionBusquedaDTO();
            dto.setId(toUuid(fila[0]));
            dto.setTitulo((String) fila[1]);
            dto.setDireccionReferencia((String) fila[2]);
            dto.setLatitud(((Number) fila[3]).doubleValue());
            dto.setLongitud(((Number) fila[4]).doubleValue());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/mapa")
    public ResponseEntity<?> publicacionesMapa(@RequestParam("lat") Double lat,
                                               @RequestParam("lng") Double lng,
                                               @RequestParam("radio_km") Double radioKm,
                                               @RequestParam(value = "material", required = false) String material,
                                               @RequestParam(value = "categoria", required = false) String categoria,
                                               @RequestParam(value = "tipo_punto", required = false) String tipoPunto) {
        String materialFiltro = blankToNull(material);
        String categoriaFiltro = blankToNull(categoria);
        String tipoPuntoFiltro = blankToNull(tipoPunto);
        String cacheKey = mapaCacheKey(lat, lng, radioKm, materialFiltro, categoriaFiltro, tipoPuntoFiltro);
        Optional<List<PublicacionMapaDTO>> cache = leerMapaCache(cacheKey);
        if (cache.isPresent()) {
            return ResponseEntity.ok(cache.get());
        }

        List<Object[]> lista = pS.publicacionesMapa(lat, lng, radioKm, materialFiltro, categoriaFiltro, tipoPuntoFiltro);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<PublicacionMapaDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            PublicacionMapaDTO dto = new PublicacionMapaDTO();
            dto.setIdPublicacion(toUuid(fila[0]));
            dto.setTitulo((String) fila[1]);
            dto.setEstado((String) fila[2]);
            dto.setDireccionReferencia((String) fila[3]);
            dto.setLatitud(toDouble(fila[4]));
            dto.setLongitud(toDouble(fila[5]));
            dto.setFechaDisponibilidad(toLocalDate(fila[6]));
            dto.setIdUsuario(toUuid(fila[7]));
            dto.setNombreUsuario((String) fila[8]);
            dto.setTipoPunto((String) fila[9]);
            dto.setMaterial((String) fila[10]);
            dto.setCategoria((String) fila[11]);
            dto.setCantidad(toDouble(fila[12]));
            dto.setUnidad((String) fila[13]);
            dto.setDistanciaKm(toDouble(fila[14]));
            dto.setTiempoEstimadoMin(calcularTiempoEstimado(dto.getDistanciaKm()));
            respuesta.add(dto);
        }

        guardarMapaCache(cacheKey, mapaParametros(lat, lng, radioKm, materialFiltro, categoriaFiltro, tipoPuntoFiltro), respuesta);
        return ResponseEntity.ok(respuesta);
    }
    @GetMapping("/cercanas")
    public ResponseEntity<?> publicacionesCercanas(@RequestParam("lat") Double lat,
                                                   @RequestParam("lng") Double lng,
                                                   @RequestParam("radio") Double radio) {
        List<Object[]> lista = pS.publicacionesCercanas(lat, lng, radio);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<PublicacionCercanaDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            PublicacionCercanaDTO dto = new PublicacionCercanaDTO();
            dto.setId(toUuid(fila[0]));
            dto.setTitulo((String) fila[1]);
            dto.setLatitud(((Number) fila[2]).doubleValue());
            dto.setLongitud(((Number) fila[3]).doubleValue());
            dto.setDistanciaKm(((Number) fila[4]).doubleValue());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }
    @GetMapping("/por-categoria/{categoria}")
    public ResponseEntity<?> publicacionesPorCategoria(@PathVariable String categoria) {
        List<Object[]> lista = pS.publicacionesPorCategoriaMaterial(categoria);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<PublicacionCategoriaDTO> respuesta = new ArrayList<>();

        for (Object[] fila : lista) {
            PublicacionCategoriaDTO dto = new PublicacionCategoriaDTO();
            dto.setIdPublicacion(toUuid(fila[0]));
            dto.setTitulo((String) fila[1]);
            dto.setLatitud(((Number) fila[2]).doubleValue());
            dto.setLongitud(((Number) fila[3]).doubleValue());
            dto.setNombreMaterial((String) fila[4]);
            dto.setCantidad(((Number) fila[5]).doubleValue());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/usuario/{idUsuario}/historial-materiales")
    @PreAuthorize("@securityPermissionService.canCreateForUser(#idUsuario)")
    public ResponseEntity<?> historialMaterialesUsuario(@PathVariable UUID idUsuario) {
        List<Object[]> lista = pS.historialMaterialesUsuario(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros");
        }

        List<HistorialMaterialUsuarioDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            HistorialMaterialUsuarioDTO dto = new HistorialMaterialUsuarioDTO();
            dto.setIdPublicacion(toUuid(fila[0]));
            dto.setTituloPublicacion((String) fila[1]);
            dto.setEstadoPublicacion((String) fila[2]);
            dto.setFechaDisponibilidad(toLocalDate(fila[3]));
            dto.setDireccionReferencia((String) fila[4]);
            dto.setNombreMaterial((String) fila[5]);
            dto.setCantidad(toDouble(fila[6]));
            dto.setUnidad((String) fila[7]);
            dto.setIdRecoleccion(toUuid(fila[8]));
            dto.setEstadoRecoleccion((String) fila[9]);
            dto.setFechaProgramada(toLocalDateTime(fila[10]));
            dto.setIdRecolector(toUuid(fila[11]));
            dto.setNombreRecolector((String) fila[12]);
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
        if (value instanceof byte[] bytes && bytes.length == 16) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            return new UUID(buffer.getLong(), buffer.getLong());
        }
        return UUID.fromString(value.toString());
    }

    private Double toDouble(Object value) {
        return value == null ? null : ((Number) value).doubleValue();
    }

    private Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private PublicacionDTO toPublicacionDto(Publicacion publicacion) {
        ModelMapper m = new ModelMapper();
        PublicacionDTO dto = m.map(publicacion, PublicacionDTO.class);
        dto.setIdUsuario(publicacion.getUsuario().getId());
        return dto;
    }

    private Integer calcularTiempoEstimado(Double distanciaKm) {
        if (distanciaKm == null) {
            return null;
        }
        return (int) Math.ceil((distanciaKm / 20.0) * 60.0);
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private String mapaCacheKey(Double lat, Double lng, Double radioKm, String material, String categoria, String tipoPunto) {
        return String.format("lat=%s|lng=%s|radio=%s|material=%s|categoria=%s|tipo=%s",
                lat, lng, radioKm, material, categoria, tipoPunto);
    }

    private String mapaParametros(Double lat, Double lng, Double radioKm, String material, String categoria, String tipoPunto) {
        return String.format("lat=%s,lng=%s,radio_km=%s,material=%s,categoria=%s,tipo_punto=%s",
                lat, lng, radioKm, material, categoria, tipoPunto);
    }

    private Optional<List<PublicacionMapaDTO>> leerMapaCache(String cacheKey) {
        Optional<MapaCache> cache = mapaCacheService.findValid(cacheKey, LocalDateTime.now());
        if (cache.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(cache.get().getResultadoJson(), new TypeReference<List<PublicacionMapaDTO>>() {
            }));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void guardarMapaCache(String cacheKey, String parametros, List<PublicacionMapaDTO> respuesta) {
        try {
            MapaCache cache = mapaCacheService.findByCacheKey(cacheKey).orElseGet(MapaCache::new);
            cache.setCacheKey(cacheKey);
            cache.setParametros(parametros);
            cache.setResultadoJson(objectMapper.writeValueAsString(respuesta));
            cache.setExpiresAt(LocalDateTime.now().plusMinutes(10));
            if (cache.getId() == null) {
                mapaCacheService.insert(cache);
            } else {
                mapaCacheService.update(cache);
            }
        } catch (Exception ignored) {
        }
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        return LocalDateTime.parse(value.toString().replace(" ", "T"));
    }

    private String validarPublicacion(PublicacionDTO dto) {
        if (dto == null) {
            return "Error: faltan datos. Debe llenar todos los campos no opcionales para realizar la publicacion";
        }
        if (dto.getIdUsuario() == null
                || isBlank(dto.getTitulo())
                || dto.getLatitud() == null
                || dto.getLongitud() == null) {
            return "Error: faltan datos. Debe llenar todos los campos no opcionales para realizar la publicacion";
        }
        if (dto.getObservaciones() != null && dto.getObservaciones().length() > 200) {
            return "Error: la observacion excede el limite de caracteres";
        }
        if (dto.getFechaDisponibilidad() != null && dto.getFechaDisponibilidad().isBefore(LocalDate.now())) {
            return "El horario seleccionado no esta disponible, por favor elige otro";
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
