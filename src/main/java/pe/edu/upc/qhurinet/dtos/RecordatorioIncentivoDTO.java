package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class RecordatorioIncentivoDTO {
    private UUID idUsuarioIncentivo;
    private UUID idIncentivo;
    private String nombreIncentivo;
    private String tipo;
    private Integer metaCantidad;
    private Integer cantidadActual;
    private String estado;
    private LocalDate fechaFin;
    private LocalDateTime completadoEn;
    private Boolean puedeReclamar;
    private Boolean proximoAVencer;

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

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
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

    public Boolean getProximoAVencer() {
        return proximoAVencer;
    }

    public void setProximoAVencer(Boolean proximoAVencer) {
        this.proximoAVencer = proximoAVencer;
    }
}
