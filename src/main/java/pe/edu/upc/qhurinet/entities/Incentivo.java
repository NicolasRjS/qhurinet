package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Incentivo")
public class Incentivo {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tipo", length = 30, nullable = false)
    private String tipo;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "costo_puntos", nullable = false)
    private Integer costoPuntos;

    @Column(name = "meta_cantidad")
    private Integer metaCantidad;

    @Column(name = "meta_unidad", length = 30)
    private String metaUnidad;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Incentivo() {
    }

    public Incentivo(UUID id, String tipo, String nombre, String descripcion, Integer costoPuntos, Integer metaCantidad, String metaUnidad, LocalDate fechaInicio, LocalDate fechaFin, Integer stock, Boolean activo, LocalDateTime createdAt) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costoPuntos = costoPuntos;
        this.metaCantidad = metaCantidad;
        this.metaUnidad = metaUnidad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.stock = stock;
        this.activo = activo;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public Integer getCostoPuntos() {
        return costoPuntos;
    }

    public void setCostoPuntos(Integer costoPuntos) {
        this.costoPuntos = costoPuntos;
    }

    public Integer getMetaCantidad() {
        return metaCantidad;
    }

    public void setMetaCantidad(Integer metaCantidad) {
        this.metaCantidad = metaCantidad;
    }

    public String getMetaUnidad() {
        return metaUnidad;
    }

    public void setMetaUnidad(String metaUnidad) {
        this.metaUnidad = metaUnidad;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
