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
@Table(name = "Calificacion", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_recoleccion"})})
public class Calificacion {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_recoleccion", nullable = false)
    private Recoleccion recoleccion;

    @ManyToOne
    @JoinColumn(name = "id_autor", nullable = false)
    private Usuario autor;

    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Calificacion() {
    }

    public Calificacion(UUID id, Recoleccion recoleccion, Usuario autor, Integer puntuacion, String comentario, LocalDateTime createdAt) {
        this.id = id;
        this.recoleccion = recoleccion;
        this.autor = autor;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
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

    public Recoleccion getRecoleccion() {
        return recoleccion;
    }

    public void setRecoleccion(Recoleccion recoleccion) {
        this.recoleccion = recoleccion;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
