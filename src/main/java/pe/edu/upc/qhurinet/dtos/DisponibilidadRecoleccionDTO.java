package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class DisponibilidadRecoleccionDTO {
    private UUID idRecoleccion;
    private LocalDateTime fechaProgramada;
    private String estado;
    private Boolean prioritaria;
    private UUID idPublicacion;
    private String tituloPublicacion;
    private UUID idRecolector;
    private String nombreRecolector;

    public UUID getIdRecoleccion() {
        return idRecoleccion;
    }

    public void setIdRecoleccion(UUID idRecoleccion) {
        this.idRecoleccion = idRecoleccion;
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

    public Boolean getPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(Boolean prioritaria) {
        this.prioritaria = prioritaria;
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
