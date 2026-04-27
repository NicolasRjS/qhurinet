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
@Table(name = "Mensaje_Chat")
public class MensajeChat {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_recoleccion", nullable = false)
    private Recoleccion recoleccion;

    @ManyToOne
    @JoinColumn(name = "id_remitente", nullable = false)
    private Usuario remitente;

    @Column(name = "contenido", columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(name = "leido", nullable = false)
    private Boolean leido;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MensajeChat() {
    }

    public MensajeChat(UUID id, Recoleccion recoleccion, Usuario remitente, String contenido, Boolean leido, LocalDateTime createdAt) {
        this.id = id;
        this.recoleccion = recoleccion;
        this.remitente = remitente;
        this.contenido = contenido;
        this.leido = leido;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.leido == null) {
            this.leido = false;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Recoleccion getRecoleccion() {
        return recoleccion;
    }

    public void setRecoleccion(Recoleccion recoleccion) {
        this.recoleccion = recoleccion;
    }

    public Usuario getRemitente() {
        return remitente;
    }

    public void setRemitente(Usuario remitente) {
        this.remitente = remitente;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
