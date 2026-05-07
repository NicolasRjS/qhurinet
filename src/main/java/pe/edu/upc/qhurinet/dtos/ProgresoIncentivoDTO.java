package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProgresoIncentivoDTO {
    private UUID idUsuarioIncentivo;
    private UUID idIncentivo;
    private String nombreIncentivo;
    private String tipo;
    private Integer metaCantidad;
    private String metaUnidad;
    private Integer cantidadActual;
    private String estado;
    private LocalDateTime completadoEn;
    private Boolean puedeReclamar;

    public UUID getIdUsuarioIncentivo() {
        return idUsuarioIncentivo;
    }

    public void setIdUsuarioIncentivo(UUID idUsuarioIncentivo) {
        this.idUsuarioIncentivo = idUsuarioIncentivo;
    }

    public UUID getIdIncentivo() {
        return idIncentivo;
    }

    public void setIdIncentivo(UUID idIncentivo) {
        this.idIncentivo = idIncentivo;
    }

    public String getNombreIncentivo() {
        return nombreIncentivo;
    }

    public void setNombreIncentivo(String nombreIncentivo) {
        this.nombreIncentivo = nombreIncentivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getMetaCantidad() {
        return metaCantidad;
    }

    public void setMetaCantidad(Integer metaCantidad) {
        this.metaCantidad = metaCantidad;
    }

    public String getMetaUnidad() {
        return metaUnidad;
    }

    public void setMetaUnidad(String metaUnidad) {
        this.metaUnidad = metaUnidad;
    }

    public Integer getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(Integer cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getCompletadoEn() {
        return completadoEn;
    }

    public void setCompletadoEn(LocalDateTime completadoEn) {
        this.completadoEn = completadoEn;
    }

    public Boolean getPuedeReclamar() {
        return puedeReclamar;
    }

    public void setPuedeReclamar(Boolean puedeReclamar) {
        this.puedeReclamar = puedeReclamar;
    }
}
