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
@Table(name = "Ruta")
public class Ruta {
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

    @Column(name = "puntos_json", columnDefinition = "TEXT", nullable = false)
    private String puntosJson;

    @Column(name = "distancia_total_km", precision = 8, scale = 2)
    private BigDecimal distanciaTotalKm;

    @Column(name = "tiempo_estimado_min")
    private Integer tiempoEstimadoMin;

    @Column(name = "favorita", nullable = false)
    private Boolean favorita;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Ruta() {
    }

    public Ruta(UUID id, Usuario usuario, String nombre, String puntosJson, BigDecimal distanciaTotalKm, Integer tiempoEstimadoMin, Boolean favorita, LocalDateTime createdAt) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.puntosJson = puntosJson;
        this.distanciaTotalKm = distanciaTotalKm;
        this.tiempoEstimadoMin = tiempoEstimadoMin;
        this.favorita = favorita;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.favorita == null) {
            this.favorita = false;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPuntosJson() {
        return puntosJson;
    }

    public void setPuntosJson(String puntosJson) {
        this.puntosJson = puntosJson;
    }

    public BigDecimal getDistanciaTotalKm() {
        return distanciaTotalKm;
    }

    public void setDistanciaTotalKm(BigDecimal distanciaTotalKm) {
        this.distanciaTotalKm = distanciaTotalKm;
    }

    public Integer getTiempoEstimadoMin() {
        return tiempoEstimadoMin;
    }

    public void setTiempoEstimadoMin(Integer tiempoEstimadoMin) {
        this.tiempoEstimadoMin = tiempoEstimadoMin;
    }

    public Boolean getFavorita() {
        return favorita;
    }

    public void setFavorita(Boolean favorita) {
        this.favorita = favorita;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
