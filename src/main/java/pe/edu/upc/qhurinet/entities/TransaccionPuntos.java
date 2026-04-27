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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Transaccion_Puntos")
public class TransaccionPuntos {
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

    @Column(name = "puntos", nullable = false)
    private Integer puntos;

    @Column(name = "motivo", length = 255, nullable = false)
    private String motivo;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private UUID referenciaId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TransaccionPuntos() {
    }

    public TransaccionPuntos(UUID id, Usuario usuario, String tipo, Integer puntos, String motivo, String referenciaTipo, UUID referenciaId, LocalDateTime createdAt) {
        this.id = id;
        this.usuario = usuario;
        this.tipo = tipo;
        this.puntos = puntos;
        this.motivo = motivo;
        this.referenciaTipo = referenciaTipo;
        this.referenciaId = referenciaId;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
