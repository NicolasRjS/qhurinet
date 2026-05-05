package pe.edu.upc.qhurinet.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class RutaDTO {
    private UUID id;
    private UUID idUsuario;
    private String nombre;
    private String puntosJson;
    private BigDecimal distanciaTotalKm;
    private Integer tiempoEstimadoMin;
    private Boolean favorita;
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
