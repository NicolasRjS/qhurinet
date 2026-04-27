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
@Table(name = "Certificado")
public class Certificado {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "nivel_dificultad", length = 30, nullable = false)
    private String nivelDificultad;

    @Column(name = "puntos_requeridos", nullable = false)
    private Integer puntosRequeridos;

    @Column(name = "url_pdf", length = 500)
    private String urlPdf;

    @Column(name = "fecha_obtencion", nullable = false)
    private LocalDateTime fechaObtencion;

    public Certificado() {
    }

    public Certificado(UUID id, Usuario usuario, String nombre, String descripcion, String nivelDificultad, Integer puntosRequeridos, String urlPdf, LocalDateTime fechaObtencion) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivelDificultad = nivelDificultad;
        this.puntosRequeridos = puntosRequeridos;
        this.urlPdf = urlPdf;
        this.fechaObtencion = fechaObtencion;
    }

    @PrePersist
    public void prePersist() {
        this.fechaObtencion = LocalDateTime.now();
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(String nivelDificultad) {
        this.nivelDificultad = nivelDificultad;
    }

    public Integer getPuntosRequeridos() {
        return puntosRequeridos;
    }

    public void setPuntosRequeridos(Integer puntosRequeridos) {
        this.puntosRequeridos = puntosRequeridos;
    }

    public String getUrlPdf() {
        return urlPdf;
    }

    public void setUrlPdf(String urlPdf) {
        this.urlPdf = urlPdf;
    }

    public LocalDateTime getFechaObtencion() {
        return fechaObtencion;
    }

    public void setFechaObtencion(LocalDateTime fechaObtencion) {
        this.fechaObtencion = fechaObtencion;
    }
}
