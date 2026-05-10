package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialPuntosSaldoDTO {
    private UUID idTransaccion;
    private LocalDateTime createdAt;
    private String tipo;
    private Integer puntos;
    private String motivo;
    private String referenciaTipo;
    private UUID referenciaId;
    private Integer saldoAcumulado;

    public UUID getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(UUID idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getReferenciaTipo() {
        return referenciaTipo;
    }

    public void setReferenciaTipo(String referenciaTipo) {
        this.referenciaTipo = referenciaTipo;
    }

    public UUID getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(UUID referenciaId) {
        this.referenciaId = referenciaId;
    }

    public Integer getSaldoAcumulado() {
        return saldoAcumulado;
    }

    public void setSaldoAcumulado(Integer saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }
}
