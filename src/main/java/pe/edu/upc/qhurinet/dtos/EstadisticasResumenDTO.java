package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class EstadisticasResumenDTO {
    private UUID idUsuario;
    private String nombre;
    private Integer puntosTotales;
    private String nivelParticipacion;
    private Long publicaciones;
    private Long entregasComoEmisor;
    private Long recojosComoRecolector;
    private Double kgReciclados;
    private Long incidencias;

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

    public Long getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(Long publicaciones) {
        this.publicaciones = publicaciones;
    }

    public Long getEntregasComoEmisor() {
        return entregasComoEmisor;
    }

    public void setEntregasComoEmisor(Long entregasComoEmisor) {
        this.entregasComoEmisor = entregasComoEmisor;
    }

    public Long getRecojosComoRecolector() {
        return recojosComoRecolector;
    }

    public void setRecojosComoRecolector(Long recojosComoRecolector) {
        this.recojosComoRecolector = recojosComoRecolector;
    }

    public Double getKgReciclados() {
        return kgReciclados;
    }

    public void setKgReciclados(Double kgReciclados) {
        this.kgReciclados = kgReciclados;
    }

    public Long getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(Long incidencias) {
        this.incidencias = incidencias;
    }
}
