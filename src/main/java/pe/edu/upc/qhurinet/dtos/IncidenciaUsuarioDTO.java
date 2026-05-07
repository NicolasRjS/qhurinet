package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class IncidenciaUsuarioDTO {
    private UUID idRecoleccion;
    private UUID idPublicacion;
    private String tituloPublicacion;
    private String incidenciaDescripcion;
    private String incidenciaEstado;
    private String incidenciaEvidenciaUrl;
    private String estadoRecoleccion;
    private LocalDateTime fechaProgramada;
    private UUID idRecolector;
    private String nombreRecolector;

    public UUID getIdRecoleccion() {
        return idRecoleccion;
    }

    public void setIdRecoleccion(UUID idRecoleccion) {
        this.idRecoleccion = idRecoleccion;
    }

    public UUID getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(UUID idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getTituloPublicacion() {
        return tituloPublicacion;
    }

    public void setTituloPublicacion(String tituloPublicacion) {
        this.tituloPublicacion = tituloPublicacion;
    }

    public String getIncidenciaDescripcion() {
        return incidenciaDescripcion;
    }

    public void setIncidenciaDescripcion(String incidenciaDescripcion) {
        this.incidenciaDescripcion = incidenciaDescripcion;
    }

    public String getIncidenciaEstado() {
        return incidenciaEstado;
    }

    public void setIncidenciaEstado(String incidenciaEstado) {
        this.incidenciaEstado = incidenciaEstado;
    }

    public String getIncidenciaEvidenciaUrl() {
        return incidenciaEvidenciaUrl;
    }

    public void setIncidenciaEvidenciaUrl(String incidenciaEvidenciaUrl) {
        this.incidenciaEvidenciaUrl = incidenciaEvidenciaUrl;
    }

    public String getEstadoRecoleccion() {
        return estadoRecoleccion;
    }

    public void setEstadoRecoleccion(String estadoRecoleccion) {
        this.estadoRecoleccion = estadoRecoleccion;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

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
}
