package pe.edu.upc.qhurinet.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransaccionDineroDTO {
    private UUID id;
    private UUID idUsuario;
    private String tipo;
    private BigDecimal monto;
    private String moneda;
    private String estado;
    private String concepto;
    private String metodoPagoTipo;
    private String metodoPagoDetalle;
    private String referenciaExterna;
    private String referenciaTipo;
    private UUID referenciaId;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getMetodoPagoTipo() {
        return metodoPagoTipo;
    }

    public void setMetodoPagoTipo(String metodoPagoTipo) {
        this.metodoPagoTipo = metodoPagoTipo;
    }

    public String getMetodoPagoDetalle() {
        return metodoPagoDetalle;
    }

    public void setMetodoPagoDetalle(String metodoPagoDetalle) {
        this.metodoPagoDetalle = metodoPagoDetalle;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public void setReferenciaExterna(String referenciaExterna) {
        this.referenciaExterna = referenciaExterna;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
