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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Publicacion")
public class Publicacion {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "estado", length = 30, nullable = false)
    private String estado;

    @Column(name = "latitud", precision = 9, scale = 6, nullable = false)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitud;

    @Column(name = "direccion_referencia", length = 255)
    private String direccionReferencia;

    @Column(name = "fecha_disponibilidad")
    private LocalDate fechaDisponibilidad;

    @Column(name = "imagenes_json", columnDefinition = "TEXT")
    private String imagenesJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Publicacion() {
    }

    public Publicacion(UUID id, Usuario usuario, String titulo, String observaciones, String estado, BigDecimal latitud, BigDecimal longitud, String direccionReferencia, LocalDate fechaDisponibilidad, String imagenesJson, LocalDateTime createdAt) {
        this.id = id;
        this.usuario = usuario;
        this.titulo = titulo;
        this.observaciones = observaciones;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccionReferencia = direccionReferencia;
        this.fechaDisponibilidad = fechaDisponibilidad;
        this.imagenesJson = imagenesJson;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "activa";
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public String getDireccionReferencia() {
        return direccionReferencia;
    }

    public void setDireccionReferencia(String direccionReferencia) {
        this.direccionReferencia = direccionReferencia;
    }

    public LocalDate getFechaDisponibilidad() {
        return fechaDisponibilidad;
    }

    public void setFechaDisponibilidad(LocalDate fechaDisponibilidad) {
        this.fechaDisponibilidad = fechaDisponibilidad;
    }

    public String getImagenesJson() {
        return imagenesJson;
    }

    public void setImagenesJson(String imagenesJson) {
        this.imagenesJson = imagenesJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
