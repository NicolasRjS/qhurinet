package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Transaccion_Dinero")
public class TransaccionDinero {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo", length = 30, nullable = false)
    private String tipo;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "moneda", length = 10, nullable = false)
    private String moneda;

    @Column(name = "estado", length = 30, nullable = false)
    private String estado;

    @Column(name = "concepto", length = 255, nullable = false)
    private String concepto;

    @Column(name = "metodo_pago_tipo", length = 50)
    private String metodoPagoTipo;

    @Column(name = "metodo_pago_detalle", length = 200)
    private String metodoPagoDetalle;

    @Column(name = "referencia_externa", length = 200)
    private String referenciaExterna;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private UUID referenciaId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TransaccionDinero() {
    }

    public TransaccionDinero(UUID id, Usuario usuario, String tipo, BigDecimal monto, String moneda, String estado, String concepto, String metodoPagoTipo, String metodoPagoDetalle, String referenciaExterna, String referenciaTipo, UUID referenciaId, LocalDateTime createdAt) {
        this.id = id;
        this.usuario = usuario;
        this.tipo = tipo;
        this.monto = monto;
        this.moneda = moneda;
        this.estado = estado;
        this.concepto = concepto;
        this.metodoPagoTipo = metodoPagoTipo;
        this.metodoPagoDetalle = metodoPagoDetalle;
        this.referenciaExterna = referenciaExterna;
        this.referenciaTipo = referenciaTipo;
        this.referenciaId = referenciaId;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.moneda == null) {
            this.moneda = "PEN";
        }
        if (this.estado == null) {
            this.estado = "pendiente";
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
