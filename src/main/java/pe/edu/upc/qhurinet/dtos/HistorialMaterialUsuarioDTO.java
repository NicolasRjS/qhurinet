package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialMaterialUsuarioDTO {
    private UUID idPublicacion;
    private String tituloPublicacion;
    private String estadoPublicacion;
    private LocalDate fechaDisponibilidad;
    private String direccionReferencia;
    private String nombreMaterial;
    private Double cantidad;
    private String unidad;
    private UUID idRecoleccion;
    private String estadoRecoleccion;
    private LocalDateTime fechaProgramada;
    private UUID idRecolector;
    private String nombreRecolector;

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

    public String getEstadoPublicacion() {
        return estadoPublicacion;
    }

    public void setEstadoPublicacion(String estadoPublicacion) {
        this.estadoPublicacion = estadoPublicacion;
    }

    public LocalDate getFechaDisponibilidad() {
        return fechaDisponibilidad;
    }

    public void setFechaDisponibilidad(LocalDate fechaDisponibilidad) {
        this.fechaDisponibilidad = fechaDisponibilidad;
    }

    public String getDireccionReferencia() {
        return direccionReferencia;
    }

    public void setDireccionReferencia(String direccionReferencia) {
        this.direccionReferencia = direccionReferencia;
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

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public UUID getIdRecoleccion() {
        return idRecoleccion;
    }

    public void setIdRecoleccion(UUID idRecoleccion) {
        this.idRecoleccion = idRecoleccion;
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
