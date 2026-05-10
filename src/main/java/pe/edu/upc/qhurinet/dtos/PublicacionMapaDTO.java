package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDate;
import java.util.UUID;

public class PublicacionMapaDTO {
    private UUID idPublicacion;
    private String titulo;
    private String estado;
    private String direccionReferencia;
    private Double latitud;
    private Double longitud;
    private LocalDate fechaDisponibilidad;
    private UUID idUsuario;
    private String nombreUsuario;
    private String tipoPunto;
    private String material;
    private String categoria;
    private Double cantidad;
    private String unidad;
    private Double distanciaKm;
    private Integer tiempoEstimadoMin;

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public LocalDate getFechaDisponibilidad() {
        return fechaDisponibilidad;
    }

    public void setFechaDisponibilidad(LocalDate fechaDisponibilidad) {
        this.fechaDisponibilidad = fechaDisponibilidad;
    }

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTipoPunto() {
        return tipoPunto;
    }

    public void setTipoPunto(String tipoPunto) {
        this.tipoPunto = tipoPunto;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public Integer getTiempoEstimadoMin() {
        return tiempoEstimadoMin;
    }

    public void setTiempoEstimadoMin(Integer tiempoEstimadoMin) {
        this.tiempoEstimadoMin = tiempoEstimadoMin;
    }
}
