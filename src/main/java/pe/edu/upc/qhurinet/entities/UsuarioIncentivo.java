package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Usuario_Incentivo", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_usuario", "id_incentivo"})})
public class UsuarioIncentivo {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_incentivo", nullable = false)
    private Incentivo incentivo;

    @Column(name = "cantidad_actual", nullable = false)
    private Integer cantidadActual;

    @Column(name = "estado", length = 30, nullable = false)
    private String estado;

    @Column(name = "completado_en")
    private LocalDateTime completadoEn;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public UsuarioIncentivo() {
    }

    public UsuarioIncentivo(UUID id, Usuario usuario, Incentivo incentivo, Integer cantidadActual, String estado, LocalDateTime completadoEn, LocalDateTime createdAt) {
        this.id = id;
        this.usuario = usuario;
        this.incentivo = incentivo;
        this.cantidadActual = cantidadActual;
        this.estado = estado;
        this.completadoEn = completadoEn;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.cantidadActual == null) {
            this.cantidadActual = 0;
        }
        if (this.estado == null) {
            this.estado = "en_progreso";
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

    public Incentivo getIncentivo() {
        return incentivo;
    }

    public void setIncentivo(Incentivo incentivo) {
        this.incentivo = incentivo;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
