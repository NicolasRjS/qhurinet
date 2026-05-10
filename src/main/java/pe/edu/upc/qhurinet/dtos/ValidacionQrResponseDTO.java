package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class ValidacionQrResponseDTO {
    private UUID idRecoleccion;
    private String estado;
    private Boolean qrValidado;
    private LocalDateTime fechaCompletada;
    private Integer puntosAcreditados;

    public UUID getIdRecoleccion() {
        return idRecoleccion;
    }

    public void setIdRecoleccion(UUID idRecoleccion) {
        this.idRecoleccion = idRecoleccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getQrValidado() {
        return qrValidado;
    }

    public void setQrValidado(Boolean qrValidado) {
        this.qrValidado = qrValidado;
    }

    public LocalDateTime getFechaCompletada() {
        return fechaCompletada;
    }

    public void setFechaCompletada(LocalDateTime fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public Integer getPuntosAcreditados() {
        return puntosAcreditados;
    }

    public void setPuntosAcreditados(Integer puntosAcreditados) {
        this.puntosAcreditados = puntosAcreditados;
    }
}
