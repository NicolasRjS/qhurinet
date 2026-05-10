package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class ActividadDetalleDTO {
    private UUID idRecoleccion;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaCompletada;
    private String estado;
    private UUID idPublicacion;
    private String tituloPublicacion;
    private String material;
    private Double cantidad;
    private String unidad;
    private String rolUsuario;
    private UUID idContraparte;
    private String nombreContraparte;
    private Boolean tieneIncidencia;
    private String incidenciaEstado;

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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
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

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public UUID getIdContraparte() {
        return idContraparte;
    }

    public void setIdContraparte(UUID idContraparte) {
        this.idContraparte = idContraparte;
    }

    public String getNombreContraparte() {
        return nombreContraparte;
    }

    public void setNombreContraparte(String nombreContraparte) {
        this.nombreContraparte = nombreContraparte;
    }

    public Boolean getTieneIncidencia() {
        return tieneIncidencia;
    }

    public void setTieneIncidencia(Boolean tieneIncidencia) {
        this.tieneIncidencia = tieneIncidencia;
    }

    public String getIncidenciaEstado() {
        return incidenciaEstado;
    }

    public void setIncidenciaEstado(String incidenciaEstado) {
        this.incidenciaEstado = incidenciaEstado;
    }
}
