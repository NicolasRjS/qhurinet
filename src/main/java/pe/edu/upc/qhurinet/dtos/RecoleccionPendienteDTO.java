package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecoleccionPendienteDTO {
    private UUID idRecoleccion;
    private UUID idPublicacion;
    private String tituloPublicacion;
    private String direccionReferencia;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fechaProgramada;
    private Boolean prioritaria;
    private String estado;
    private String nombreEmisor;

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

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public Boolean getPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(Boolean prioritaria) {
        this.prioritaria = prioritaria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreEmisor() {
        return nombreEmisor;
    }

    public void setNombreEmisor(String nombreEmisor) {
        this.nombreEmisor = nombreEmisor;
    }
}
