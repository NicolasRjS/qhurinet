# QhuriNet — Queries por Implementar (10 queries / 5 personas)

> Cada persona del grupo implementa 2 queries en su rama. Al terminar, hacer Pull Request hacia develop. El líder del grupo hará merge final.
>
> **Antes de empezar:** asegúrate de tener tu rama `dev/<tu-nombre>` actualizada con develop (`git pull origin develop`).
>
> **Estilo usado:** igual al proyecto de referencia real de la profesora: `@Query(nativeQuery = true)` en Repository, retorno `List<Object[]>`, Service Interface y ServiceImplement devuelven/delegan `List<Object[]>`, y el Controller arma los DTOs con casts manuales.

---

## Alumno 1 — [Nombre]

### Query 1: Top 5 materiales más reciclados (US 24-EP3 / 43-EP6)

**Archivos a crear o modificar:**
- `dtos/MaterialTopDTO.java` (nuevo)
- `repositories/IMaterialRepository.java` (modificar)
- `servicesinterfaces/IMaterialService.java` (modificar)
- `servicesimplements/MaterialServiceImplement.java` (modificar)
- `controllers/MaterialController.java` (modificar)

**1) DTO — `dtos/MaterialTopDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

public class MaterialTopDTO {
    private String nombreMaterial;
    private String categoria;
    private Double totalKg;

    public String getNombreMaterial() {
        return nombreMaterial;
    }

    public void setNombreMaterial(String nombreMaterial) {
        this.nombreMaterial = nombreMaterial;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getTotalKg() {
        return totalKg;
    }

    public void setTotalKg(Double totalKg) {
        this.totalKg = totalKg;
    }
}
```

**2) Repository — agregar imports y método en `IMaterialRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Query(value = "\n" +
        "SELECT m.nombre, m.categoria, COALESCE(SUM(pm.cantidad), 0) AS total_kg\n" +
        " FROM material m\n" +
        " INNER JOIN publicacion_material pm ON m.id = pm.id_material\n" +
        " GROUP BY m.id, m.nombre, m.categoria\n" +
        " ORDER BY total_kg DESC\n" +
        " LIMIT 5",
        nativeQuery = true)
public List<Object[]> top5MaterialesMasReciclados();
```

**3) Service Interface — agregar método en `IMaterialService.java`**
```java
public List<Object[]> top5MaterialesMasReciclados();
```

**4) ServiceImplement — agregar método en `MaterialServiceImplement.java`**
```java
@Override
public List<Object[]> top5MaterialesMasReciclados() {
    return mR.top5MaterialesMasReciclados();
}
```

**5) Controller — agregar imports y endpoint en `MaterialController.java`**
```java
import pe.edu.upc.qhurinet.dtos.MaterialTopDTO;

import java.util.ArrayList;

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
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/materiales/top5
```

---

### Query 2: Buscar publicaciones por nombre o dirección (US 28-EP4)

**Archivos a crear o modificar:**
- `dtos/PublicacionBusquedaDTO.java` (nuevo)
- `repositories/IPublicacionRepository.java` (modificar)
- `servicesinterfaces/IPublicacionService.java` (modificar)
- `servicesimplements/PublicacionServiceImplement.java` (modificar)
- `controllers/PublicacionController.java` (modificar)

**1) DTO — `dtos/PublicacionBusquedaDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class PublicacionBusquedaDTO {
    private UUID id;
    private String titulo;
    private String direccionReferencia;
    private Double latitud;
    private Double longitud;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDireccionReferencia() {
        return direccionReferencia;
    }

    public void setDireccionReferencia(String direccionReferencia) {
        this.direccionReferencia = direccionReferencia;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
```

**2) Repository — agregar imports y método en `IPublicacionRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Query(value = "\n" +
        "SELECT p.id, p.titulo, p.direccion_referencia, p.latitud, p.longitud\n" +
        " FROM publicacion p\n" +
        " WHERE p.estado = 'activa'\n" +
        " AND (p.titulo ILIKE CONCAT('%', :texto, '%')\n" +
        " OR p.direccion_referencia ILIKE CONCAT('%', :texto, '%'))\n" +
        " ORDER BY p.created_at DESC",
        nativeQuery = true)
public List<Object[]> buscarPublicacionesPorTexto(@Param("texto") String texto);
```

**3) Service Interface — agregar método en `IPublicacionService.java`**
```java
public List<Object[]> buscarPublicacionesPorTexto(String texto);
```

**4) ServiceImplement — agregar método en `PublicacionServiceImplement.java`**
```java
@Override
public List<Object[]> buscarPublicacionesPorTexto(String texto) {
    return pR.buscarPublicacionesPorTexto(texto);
}
```

**5) Controller — agregar imports y endpoint en `PublicacionController.java`**
```java
import pe.edu.upc.qhurinet.dtos.PublicacionBusquedaDTO;

import java.util.ArrayList;

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
        dto.setId((UUID) fila[0]);
        dto.setTitulo((String) fila[1]);
        dto.setDireccionReferencia((String) fila[2]);
        dto.setLatitud(((Number) fila[3]).doubleValue());
        dto.setLongitud(((Number) fila[4]).doubleValue());
        respuesta.add(dto);
    }

    return ResponseEntity.ok(respuesta);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/publicaciones/buscar?q=miraflores
```

---

## Alumno 2 — [Nombre]

### Query 3: Promedio de calificación de un recolector (US 10-EP2)

**Archivos a crear o modificar:**
- `dtos/PromedioRecolectorDTO.java` (nuevo)
- `repositories/IRecoleccionRepository.java` (modificar)
- `servicesinterfaces/IRecoleccionService.java` (modificar)
- `servicesimplements/RecoleccionServiceImplement.java` (modificar)
- `controllers/RecoleccionController.java` (modificar)

**1) DTO — `dtos/PromedioRecolectorDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class PromedioRecolectorDTO {
    private UUID idRecolector;
    private String nombreRecolector;
    private Double puntuacionPromedio;
    private Long totalRecolecciones;

    public UUID getIdRecolector() {
        return idRecolector;
    }

    public void setIdRecolector(UUID idRecolector) {
        this.idRecolector = idRecolector;
    }

    public String getNombreRecolector() {
        return nombreRecolector;
    }

    public void setNombreRecolector(String nombreRecolector) {
        this.nombreRecolector = nombreRecolector;
    }

    public Double getPuntuacionPromedio() {
        return puntuacionPromedio;
    }

    public void setPuntuacionPromedio(Double puntuacionPromedio) {
        this.puntuacionPromedio = puntuacionPromedio;
    }

    public Long getTotalRecolecciones() {
        return totalRecolecciones;
    }

    public void setTotalRecolecciones(Long totalRecolecciones) {
        this.totalRecolecciones = totalRecolecciones;
    }
}
```

**2) Repository — agregar imports y método en `IRecoleccionRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Query(value = "\n" +
        "SELECT u.id, u.nombre, COALESCE(AVG(c.puntuacion), 0) AS puntuacion_promedio,\n" +
        " COUNT(r.id) AS total_recolecciones\n" +
        " FROM usuario u\n" +
        " INNER JOIN recoleccion r ON u.id = r.id_recolector\n" +
        " INNER JOIN calificacion c ON r.id = c.id_recoleccion\n" +
        " WHERE u.id = :idRecolector\n" +
        " AND r.estado = 'completada'\n" +
        " GROUP BY u.id, u.nombre",
        nativeQuery = true)
public List<Object[]> promedioCalificacionRecolector(@Param("idRecolector") UUID idRecolector);
```

**3) Service Interface — agregar método en `IRecoleccionService.java`**
```java
public List<Object[]> promedioCalificacionRecolector(UUID idRecolector);
```

**4) ServiceImplement — agregar método en `RecoleccionServiceImplement.java`**
```java
@Override
public List<Object[]> promedioCalificacionRecolector(UUID idRecolector) {
    return rR.promedioCalificacionRecolector(idRecolector);
}
```

**5) Controller — agregar imports y endpoint en `RecoleccionController.java`**
```java
import pe.edu.upc.qhurinet.dtos.PromedioRecolectorDTO;

@GetMapping("/promedio-recolector/{idRecolector}")
public ResponseEntity<?> promedioRecolector(@PathVariable UUID idRecolector) {
    List<Object[]> lista = rS.promedioCalificacionRecolector(idRecolector);

    if (lista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No hay registros");
    }

    Object[] fila = lista.get(0);
    PromedioRecolectorDTO dto = new PromedioRecolectorDTO();
    dto.setIdRecolector((UUID) fila[0]);
    dto.setNombreRecolector((String) fila[1]);
    dto.setPuntuacionPromedio(((Number) fila[2]).doubleValue());
    dto.setTotalRecolecciones(((Number) fila[3]).longValue());

    return ResponseEntity.ok(dto);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/recolecciones/promedio-recolector/00000000-0000-0000-0000-000000000000
```

---

### Query 4: Recolecciones por rango de fechas y estado (US 45-EP6)

**Archivos a crear o modificar:**
- `dtos/RecoleccionRangoDTO.java` (nuevo)
- `repositories/IRecoleccionRepository.java` (modificar)
- `servicesinterfaces/IRecoleccionService.java` (modificar)
- `servicesimplements/RecoleccionServiceImplement.java` (modificar)
- `controllers/RecoleccionController.java` (modificar)

**1) DTO — `dtos/RecoleccionRangoDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecoleccionRangoDTO {
    private UUID id;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaCompletada;
    private String estado;
    private String tituloPublicacion;
    private String nombreRecolector;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDateTime getFechaCompletada() {
        return fechaCompletada;
    }

    public void setFechaCompletada(LocalDateTime fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTituloPublicacion() {
        return tituloPublicacion;
    }

    public void setTituloPublicacion(String tituloPublicacion) {
        this.tituloPublicacion = tituloPublicacion;
    }

    public String getNombreRecolector() {
        return nombreRecolector;
    }

    public void setNombreRecolector(String nombreRecolector) {
        this.nombreRecolector = nombreRecolector;
    }
}
```

**2) Repository — agregar imports y método en `IRecoleccionRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Query(value = "\n" +
        "SELECT r.id, r.fecha_programada, r.fecha_completada, r.estado,\n" +
        " p.titulo, u.nombre\n" +
        " FROM recoleccion r\n" +
        " INNER JOIN publicacion p ON r.id_publicacion = p.id\n" +
        " INNER JOIN usuario u ON r.id_recolector = u.id\n" +
        " WHERE CAST(r.fecha_programada AS date) BETWEEN :fechaIni AND :fechaFin\n" +
        " AND (:estado IS NULL OR r.estado = :estado)\n" +
        " ORDER BY r.fecha_programada ASC",
        nativeQuery = true)
public List<Object[]> recoleccionesPorRangoYEstado(@Param("fechaIni") LocalDate fechaIni,
                                                   @Param("fechaFin") LocalDate fechaFin,
                                                   @Param("estado") String estado);
```

**3) Service Interface — agregar método en `IRecoleccionService.java`**
```java
public List<Object[]> recoleccionesPorRangoYEstado(LocalDate fechaIni, LocalDate fechaFin, String estado);
```

**4) ServiceImplement — agregar método en `RecoleccionServiceImplement.java`**
```java
@Override
public List<Object[]> recoleccionesPorRangoYEstado(LocalDate fechaIni, LocalDate fechaFin, String estado) {
    return rR.recoleccionesPorRangoYEstado(fechaIni, fechaFin, estado);
}
```

**5) Controller — agregar imports y endpoint en `RecoleccionController.java`**
```java
import pe.edu.upc.qhurinet.dtos.RecoleccionRangoDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@GetMapping("/rango")
public ResponseEntity<?> recoleccionesPorRango(@RequestParam("fechaIni") LocalDate fechaIni,
                                               @RequestParam("fechaFin") LocalDate fechaFin,
                                               @RequestParam(value = "estado", required = false) String estado) {
    List<Object[]> lista = rS.recoleccionesPorRangoYEstado(fechaIni, fechaFin, estado);

    if (lista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No hay registros");
    }

    List<RecoleccionRangoDTO> respuesta = new ArrayList<>();

    for (Object[] fila : lista) {
        RecoleccionRangoDTO dto = new RecoleccionRangoDTO();
        dto.setId((UUID) fila[0]);
        dto.setFechaProgramada((LocalDateTime) fila[1]);
        dto.setFechaCompletada((LocalDateTime) fila[2]);
        dto.setEstado((String) fila[3]);
        dto.setTituloPublicacion((String) fila[4]);
        dto.setNombreRecolector((String) fila[5]);
        respuesta.add(dto);
    }

    return ResponseEntity.ok(respuesta);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/recolecciones/rango?fechaIni=2026-05-01&fechaFin=2026-05-31&estado=completada
```

---

## Alumno 3 — [Nombre]

### Query 5: Total de puntos ganados por usuario en un mes (US 15-EP3)

**Archivos a crear o modificar:**
- `dtos/PuntosMesUsuarioDTO.java` (nuevo)
- `repositories/ITransaccionPuntosRepository.java` (modificar)
- `servicesinterfaces/ITransaccionPuntosService.java` (modificar)
- `servicesimplements/TransaccionPuntosServiceImplement.java` (modificar)
- `controllers/TransaccionPuntosController.java` (modificar)

**1) DTO — `dtos/PuntosMesUsuarioDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class PuntosMesUsuarioDTO {
    private UUID idUsuario;
    private String nombre;
    private Integer totalPuntosMes;

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getTotalPuntosMes() {
        return totalPuntosMes;
    }

    public void setTotalPuntosMes(Integer totalPuntosMes) {
        this.totalPuntosMes = totalPuntosMes;
    }
}
```

**2) Repository — agregar imports y método en `ITransaccionPuntosRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Query(value = "\n" +
        "SELECT u.id, u.nombre, COALESCE(SUM(tp.puntos), 0) AS total_puntos_mes\n" +
        " FROM usuario u\n" +
        " LEFT JOIN transaccion_puntos tp ON u.id = tp.id_usuario\n" +
        " AND tp.tipo = 'ganado'\n" +
        " AND TO_CHAR(tp.created_at, 'YYYY-MM') = :mes\n" +
        " WHERE u.id = :idUsuario\n" +
        " GROUP BY u.id, u.nombre",
        nativeQuery = true)
public List<Object[]> totalPuntosGanadosPorMes(@Param("idUsuario") UUID idUsuario,
                                               @Param("mes") String mes);
```

**3) Service Interface — agregar método en `ITransaccionPuntosService.java`**
```java
public List<Object[]> totalPuntosGanadosPorMes(UUID idUsuario, String mes);
```

**4) ServiceImplement — agregar método en `TransaccionPuntosServiceImplement.java`**
```java
@Override
public List<Object[]> totalPuntosGanadosPorMes(UUID idUsuario, String mes) {
    return tR.totalPuntosGanadosPorMes(idUsuario, mes);
}
```

**5) Controller — agregar imports y endpoint en `TransaccionPuntosController.java`**
```java
import pe.edu.upc.qhurinet.dtos.PuntosMesUsuarioDTO;

@GetMapping("/total-mes/{idUsuario}")
public ResponseEntity<?> totalPuntosMes(@PathVariable UUID idUsuario,
                                        @RequestParam("mes") String mes) {
    List<Object[]> lista = tS.totalPuntosGanadosPorMes(idUsuario, mes);

    if (lista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No hay registros");
    }

    Object[] fila = lista.get(0);
    PuntosMesUsuarioDTO dto = new PuntosMesUsuarioDTO();
    dto.setIdUsuario((UUID) fila[0]);
    dto.setNombre((String) fila[1]);
    dto.setTotalPuntosMes(((Number) fila[2]).intValue());

    return ResponseEntity.ok(dto);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/transacciones-puntos/total-mes/00000000-0000-0000-0000-000000000000?mes=2026-05
```

---

### Query 6: Top 10 usuarios con más puntos / ranking (US 23-EP3)

**Archivos a crear o modificar:**
- `dtos/UsuarioRankingDTO.java` (nuevo)
- `repositories/IUsuarioRepository.java` (modificar)
- `servicesinterfaces/IUsuarioService.java` (modificar)
- `servicesimplements/UsuarioServiceImplement.java` (modificar)
- `controllers/UsuarioController.java` (modificar)

**1) DTO — `dtos/UsuarioRankingDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class UsuarioRankingDTO {
    private UUID idUsuario;
    private String nombre;
    private Integer puntosTotales;
    private String nivelParticipacion;

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public String getNivelParticipacion() {
        return nivelParticipacion;
    }

    public void setNivelParticipacion(String nivelParticipacion) {
        this.nivelParticipacion = nivelParticipacion;
    }
}
```

**2) Repository — agregar imports y método en `IUsuarioRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Query(value = "\n" +
        "SELECT u.id, u.nombre, u.puntos_totales, u.nivel_participacion\n" +
        " FROM usuario u\n" +
        " ORDER BY u.puntos_totales DESC\n" +
        " LIMIT 10",
        nativeQuery = true)
public List<Object[]> rankingUsuariosPorPuntos();
```

**3) Service Interface — agregar método en `IUsuarioService.java`**
```java
public List<Object[]> rankingUsuariosPorPuntos();
```

**4) ServiceImplement — agregar método en `UsuarioServiceImplement.java`**
```java
@Override
public List<Object[]> rankingUsuariosPorPuntos() {
    return uR.rankingUsuariosPorPuntos();
}
```

**5) Controller — agregar imports y endpoint en `UsuarioController.java`**
```java
import pe.edu.upc.qhurinet.dtos.UsuarioRankingDTO;

@GetMapping("/ranking")
public ResponseEntity<?> rankingUsuarios() {
    List<Object[]> lista = uS.rankingUsuariosPorPuntos();

    if (lista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No hay registros");
    }

    List<UsuarioRankingDTO> respuesta = new ArrayList<>();

    for (Object[] fila : lista) {
        UsuarioRankingDTO dto = new UsuarioRankingDTO();
        dto.setIdUsuario((UUID) fila[0]);
        dto.setNombre((String) fila[1]);
        dto.setPuntosTotales(((Number) fila[2]).intValue());
        dto.setNivelParticipacion((String) fila[3]);
        respuesta.add(dto);
    }

    return ResponseEntity.ok(respuesta);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/usuarios/ranking
```

---

## Alumno 4 — [Nombre]

### Query 7: Publicaciones activas dentro de un radio Haversine (US 26-EP4)

**Archivos a crear o modificar:**
- `dtos/PublicacionCercanaDTO.java` (nuevo)
- `repositories/IPublicacionRepository.java` (modificar)
- `servicesinterfaces/IPublicacionService.java` (modificar)
- `servicesimplements/PublicacionServiceImplement.java` (modificar)
- `controllers/PublicacionController.java` (modificar)

**1) DTO — `dtos/PublicacionCercanaDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class PublicacionCercanaDTO {
    private UUID id;
    private String titulo;
    private Double latitud;
    private Double longitud;
    private Double distanciaKm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
}
```

**2) Repository — agregar imports y método en `IPublicacionRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Query(value = "\n" +
        "SELECT p.id, p.titulo, p.latitud, p.longitud,\n" +
        " (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitud)) *\n" +
        " cos(radians(p.longitud) - radians(:lng)) +\n" +
        " sin(radians(:lat)) * sin(radians(p.latitud)))) AS distancia_km\n" +
        " FROM publicacion p\n" +
        " WHERE p.estado = 'activa'\n" +
        " GROUP BY p.id, p.titulo, p.latitud, p.longitud\n" +
        " HAVING (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitud)) *\n" +
        " cos(radians(p.longitud) - radians(:lng)) +\n" +
        " sin(radians(:lat)) * sin(radians(p.latitud)))) <= :radio\n" +
        " ORDER BY distancia_km ASC",
        nativeQuery = true)
public List<Object[]> publicacionesCercanas(@Param("lat") Double lat,
                                            @Param("lng") Double lng,
                                            @Param("radio") Double radio);
```

**3) Service Interface — agregar método en `IPublicacionService.java`**
```java
public List<Object[]> publicacionesCercanas(Double lat, Double lng, Double radio);
```

**4) ServiceImplement — agregar método en `PublicacionServiceImplement.java`**
```java
@Override
public List<Object[]> publicacionesCercanas(Double lat, Double lng, Double radio) {
    return pR.publicacionesCercanas(lat, lng, radio);
}
```

**5) Controller — agregar imports y endpoint en `PublicacionController.java`**
```java
import pe.edu.upc.qhurinet.dtos.PublicacionCercanaDTO;

import java.util.ArrayList;

@GetMapping("/cercanas")
public ResponseEntity<?> publicacionesCercanas(@RequestParam("lat") Double lat,
                                               @RequestParam("lng") Double lng,
                                               @RequestParam("radio") Double radio) {
    List<Object[]> lista = pS.publicacionesCercanas(lat, lng, radio);

    if (lista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No hay registros");
    }

    List<PublicacionCercanaDTO> respuesta = new ArrayList<>();

    for (Object[] fila : lista) {
        PublicacionCercanaDTO dto = new PublicacionCercanaDTO();
        dto.setId((UUID) fila[0]);
        dto.setTitulo((String) fila[1]);
        dto.setLatitud(((Number) fila[2]).doubleValue());
        dto.setLongitud(((Number) fila[3]).doubleValue());
        dto.setDistanciaKm(((Number) fila[4]).doubleValue());
        respuesta.add(dto);
    }

    return ResponseEntity.ok(respuesta);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/publicaciones/cercanas?lat=-12.0464&lng=-77.0428&radio=5
```

---

### Query 8: Listar publicaciones por categoría de material (US 27-EP4)

**Archivos a crear o modificar:**
- `dtos/PublicacionCategoriaDTO.java` (nuevo)
- `repositories/IPublicacionRepository.java` (modificar)
- `servicesinterfaces/IPublicacionService.java` (modificar)
- `servicesimplements/PublicacionServiceImplement.java` (modificar)
- `controllers/PublicacionController.java` (modificar)

**1) DTO — `dtos/PublicacionCategoriaDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class PublicacionCategoriaDTO {
    private UUID idPublicacion;
    private String titulo;
    private Double latitud;
    private Double longitud;
    private String nombreMaterial;
    private Double cantidad;

    public UUID getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(UUID idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getNombreMaterial() {
        return nombreMaterial;
    }

    public void setNombreMaterial(String nombreMaterial) {
        this.nombreMaterial = nombreMaterial;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }
}
```

**2) Repository — agregar imports y método en `IPublicacionRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Query(value = "\n" +
        "SELECT p.id, p.titulo, p.latitud, p.longitud, m.nombre, pm.cantidad\n" +
        " FROM publicacion p\n" +
        " INNER JOIN publicacion_material pm ON p.id = pm.id_publicacion\n" +
        " INNER JOIN material m ON pm.id_material = m.id\n" +
        " WHERE p.estado = 'activa'\n" +
        " AND LOWER(m.categoria) = LOWER(:categoria)\n" +
        " ORDER BY p.created_at DESC",
        nativeQuery = true)
public List<Object[]> publicacionesPorCategoriaMaterial(@Param("categoria") String categoria);
```

**3) Service Interface — agregar método en `IPublicacionService.java`**
```java
public List<Object[]> publicacionesPorCategoriaMaterial(String categoria);
```

**4) ServiceImplement — agregar método en `PublicacionServiceImplement.java`**
```java
@Override
public List<Object[]> publicacionesPorCategoriaMaterial(String categoria) {
    return pR.publicacionesPorCategoriaMaterial(categoria);
}
```

**5) Controller — agregar imports y endpoint en `PublicacionController.java`**
```java
import pe.edu.upc.qhurinet.dtos.PublicacionCategoriaDTO;

import java.util.ArrayList;

@GetMapping("/por-categoria/{categoria}")
public ResponseEntity<?> publicacionesPorCategoria(@PathVariable String categoria) {
    List<Object[]> lista = pS.publicacionesPorCategoriaMaterial(categoria);

    if (lista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No hay registros");
    }

    List<PublicacionCategoriaDTO> respuesta = new ArrayList<>();

    for (Object[] fila : lista) {
        PublicacionCategoriaDTO dto = new PublicacionCategoriaDTO();
        dto.setIdPublicacion((UUID) fila[0]);
        dto.setTitulo((String) fila[1]);
        dto.setLatitud(((Number) fila[2]).doubleValue());
        dto.setLongitud(((Number) fila[3]).doubleValue());
        dto.setNombreMaterial((String) fila[4]);
        dto.setCantidad(((Number) fila[5]).doubleValue());
        respuesta.add(dto);
    }

    return ResponseEntity.ok(respuesta);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/publicaciones/por-categoria/plastico
```

---

## Alumno 5 — [Nombre]

### Query 9: Historial completo de un usuario con filtros (US 45-EP6)

**Archivos a crear o modificar:**
- `dtos/HistorialRecoleccionDTO.java` (nuevo)
- `repositories/IRecoleccionRepository.java` (modificar)
- `servicesinterfaces/IRecoleccionService.java` (modificar)
- `servicesimplements/RecoleccionServiceImplement.java` (modificar)
- `controllers/RecoleccionController.java` (modificar)

**1) DTO — `dtos/HistorialRecoleccionDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialRecoleccionDTO {
    private UUID id;
    private LocalDateTime fechaProgramada;
    private String estado;
    private String tituloPublicacion;
    private String rolUsuario;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTituloPublicacion() {
        return tituloPublicacion;
    }

    public void setTituloPublicacion(String tituloPublicacion) {
        this.tituloPublicacion = tituloPublicacion;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }
}
```

**2) Repository — agregar imports y método en `IRecoleccionRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Query(value = "\n" +
        "SELECT r.id, r.fecha_programada, r.estado, p.titulo,\n" +
        " CASE WHEN p.id_usuario = :idUsuario THEN 'emisor' ELSE 'recolector' END AS rol_usuario\n" +
        " FROM recoleccion r\n" +
        " INNER JOIN publicacion p ON r.id_publicacion = p.id\n" +
        " WHERE (p.id_usuario = :idUsuario OR r.id_recolector = :idUsuario)\n" +
        " AND (:fechaIni IS NULL OR CAST(r.fecha_programada AS date) >= :fechaIni)\n" +
        " AND (:fechaFin IS NULL OR CAST(r.fecha_programada AS date) <= :fechaFin)\n" +
        " AND (:estado IS NULL OR r.estado = :estado)\n" +
        " ORDER BY r.fecha_programada DESC",
        nativeQuery = true)
public List<Object[]> historialUsuario(@Param("idUsuario") UUID idUsuario,
                                       @Param("fechaIni") LocalDate fechaIni,
                                       @Param("fechaFin") LocalDate fechaFin,
                                       @Param("estado") String estado);
```

**3) Service Interface — agregar método en `IRecoleccionService.java`**
```java
public List<Object[]> historialUsuario(UUID idUsuario, LocalDate fechaIni, LocalDate fechaFin, String estado);
```

**4) ServiceImplement — agregar método en `RecoleccionServiceImplement.java`**
```java
@Override
public List<Object[]> historialUsuario(UUID idUsuario, LocalDate fechaIni, LocalDate fechaFin, String estado) {
    return rR.historialUsuario(idUsuario, fechaIni, fechaFin, estado);
}
```

**5) Controller — agregar imports y endpoint en `RecoleccionController.java`**
```java
import pe.edu.upc.qhurinet.dtos.HistorialRecoleccionDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@GetMapping("/historial/{idUsuario}")
public ResponseEntity<?> historialUsuario(@PathVariable UUID idUsuario,
                                          @RequestParam(value = "fechaIni", required = false) LocalDate fechaIni,
                                          @RequestParam(value = "fechaFin", required = false) LocalDate fechaFin,
                                          @RequestParam(value = "estado", required = false) String estado) {
    List<Object[]> lista = rS.historialUsuario(idUsuario, fechaIni, fechaFin, estado);

    if (lista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No hay registros");
    }

    List<HistorialRecoleccionDTO> respuesta = new ArrayList<>();

    for (Object[] fila : lista) {
        HistorialRecoleccionDTO dto = new HistorialRecoleccionDTO();
        dto.setId((UUID) fila[0]);
        dto.setFechaProgramada((LocalDateTime) fila[1]);
        dto.setEstado((String) fila[2]);
        dto.setTituloPublicacion((String) fila[3]);
        dto.setRolUsuario((String) fila[4]);
        respuesta.add(dto);
    }

    return ResponseEntity.ok(respuesta);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/recolecciones/historial/00000000-0000-0000-0000-000000000000?fechaIni=2026-05-01&fechaFin=2026-05-31&estado=completada
```

---

### Query 10: Total kg reciclados por mes (US 43-EP6)

**Archivos a crear o modificar:**
- `dtos/KgPorMesDTO.java` (nuevo)
- `repositories/IUsuarioRepository.java` (modificar)
- `servicesinterfaces/IUsuarioService.java` (modificar)
- `servicesimplements/UsuarioServiceImplement.java` (modificar)
- `controllers/UsuarioController.java` (modificar)

**1) DTO — `dtos/KgPorMesDTO.java`**
```java
package pe.edu.upc.qhurinet.dtos;

public class KgPorMesDTO {
    private String mes;
    private Double totalKg;

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Double getTotalKg() {
        return totalKg;
    }

    public void setTotalKg(Double totalKg) {
        this.totalKg = totalKg;
    }
}
```

**2) Repository — agregar imports y método en `IUsuarioRepository.java`**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Query(value = "\n" +
        "SELECT TO_CHAR(r.fecha_completada, 'YYYY-MM') AS mes,\n" +
        " COALESCE(SUM(pm.cantidad), 0) AS total_kg\n" +
        " FROM recoleccion r\n" +
        " INNER JOIN publicacion p ON r.id_publicacion = p.id\n" +
        " INNER JOIN publicacion_material pm ON p.id = pm.id_publicacion\n" +
        " WHERE r.estado = 'completada'\n" +
        " AND (r.id_recolector = :idUsuario OR p.id_usuario = :idUsuario)\n" +
        " AND r.fecha_completada >= (CURRENT_DATE - INTERVAL '6 months')\n" +
        " GROUP BY EXTRACT(YEAR FROM r.fecha_completada),\n" +
        " EXTRACT(MONTH FROM r.fecha_completada),\n" +
        " TO_CHAR(r.fecha_completada, 'YYYY-MM')\n" +
        " ORDER BY EXTRACT(YEAR FROM r.fecha_completada) ASC,\n" +
        " EXTRACT(MONTH FROM r.fecha_completada) ASC",
        nativeQuery = true)
public List<Object[]> kgRecicladosPorMes(@Param("idUsuario") UUID idUsuario);
```

**3) Service Interface — agregar método en `IUsuarioService.java`**
```java
public List<Object[]> kgRecicladosPorMes(UUID idUsuario);
```

**4) ServiceImplement — agregar método en `UsuarioServiceImplement.java`**
```java
@Override
public List<Object[]> kgRecicladosPorMes(UUID idUsuario) {
    return uR.kgRecicladosPorMes(idUsuario);
}
```

**5) Controller — agregar imports y endpoint en `UsuarioController.java`**
```java
import pe.edu.upc.qhurinet.dtos.KgPorMesDTO;

@GetMapping("/{idUsuario}/estadisticas/kg-por-mes")
public ResponseEntity<?> kgRecicladosPorMes(@PathVariable UUID idUsuario) {
    List<Object[]> lista = uS.kgRecicladosPorMes(idUsuario);

    if (lista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No hay registros");
    }

    List<KgPorMesDTO> respuesta = new ArrayList<>();

    for (Object[] fila : lista) {
        KgPorMesDTO dto = new KgPorMesDTO();
        dto.setMes((String) fila[0]);
        dto.setTotalKg(((Number) fila[1]).doubleValue());
        respuesta.add(dto);
    }

    return ResponseEntity.ok(respuesta);
}
```

**6) Ejemplo de prueba (Postman / cURL)**
```http
GET http://localhost:8083/api/usuarios/00000000-0000-0000-0000-000000000000/estadisticas/kg-por-mes
```

---

## Notas finales

- Todas las queries respetan el estilo real comprobado del proyecto de referencia: SQL nativo, retorno `List<Object[]>`, ServiceImplement solo delega al Repository, y casts manuales en Controller.
- Para usar `@Query` y `@Param`, agrega los imports indicados en cada Repository.
- Para métodos con `UUID`, importa `java.util.UUID` si el archivo aún no lo tiene.
- Para métodos con `LocalDate`, importa `java.time.LocalDate`; para casts de fechas devueltas por SQL, usa `LocalDateTime` cuando la columna sea timestamp.
- Para números de `SUM`, `AVG` y `COUNT`, usa siempre `((Number) fila[i]).doubleValue()`, `.longValue()` o `.intValue()` según corresponda.
- Si la query devuelve lista vacía, el Controller devuelve `ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay registros")`, como el endpoint de consultas del proyecto de referencia.
- Antes de hacer commit, ejecuta el proyecto y prueba el endpoint con Postman o Swagger.
- Commit con mensaje claro: `feat: implementa Query X — descripcion breve (US-XX)`.
