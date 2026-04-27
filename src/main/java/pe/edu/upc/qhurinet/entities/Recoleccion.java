package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Recoleccion")
public class Recoleccion {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_publicacion", nullable = false)
    private Publicacion publicacion;

    @ManyToOne
    @JoinColumn(name = "id_recolector", nullable = false)
    private Usuario recolector;

    @Column(name = "estado", length = 30, nullable = false)
    private String estado;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @Column(name = "prioritaria", nullable = false)
    private Boolean prioritaria;

    @Column(name = "codigo_qr", length = 500, unique = true)
    private String codigoQr;

    @Column(name = "qr_validado", nullable = false)
    private Boolean qrValidado;

    @Column(name = "lat_recolector", precision = 9, scale = 6)
    private BigDecimal latRecolector;

    @Column(name = "lng_recolector", precision = 9, scale = 6)
    private BigDecimal lngRecolector;

    @Column(name = "incidencia_descripcion", columnDefinition = "TEXT")
    private String incidenciaDescripcion;

    @Column(name = "incidencia_estado", length = 30)
    private String incidenciaEstado;

    @Column(name = "incidencia_evidencia_url", length = 500)
    private String incidenciaEvidenciaUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Recoleccion() {
    }

    public Recoleccion(UUID id, Publicacion publicacion, Usuario recolector, String estado, LocalDateTime fechaProgramada, LocalDateTime fechaCompletada, Boolean prioritaria, String codigoQr, Boolean qrValidado, BigDecimal latRecolector, BigDecimal lngRecolector, String incidenciaDescripcion, String incidenciaEstado, String incidenciaEvidenciaUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.publicacion = publicacion;
        this.recolector = recolector;
        this.estado = estado;
        this.fechaProgramada = fechaProgramada;
        this.fechaCompletada = fechaCompletada;
        this.prioritaria = prioritaria;
        this.codigoQr = codigoQr;
        this.qrValidado = qrValidado;
        this.latRecolector = latRecolector;
        this.lngRecolector = lngRecolector;
        this.incidenciaDescripcion = incidenciaDescripcion;
        this.incidenciaEstado = incidenciaEstado;
        this.incidenciaEvidenciaUrl = incidenciaEvidenciaUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime fechaActual = LocalDateTime.now();
        this.createdAt = fechaActual;
        this.updatedAt = fechaActual;
        if (this.estado == null) {
            this.estado = "programada";
        }
        if (this.prioritaria == null) {
            this.prioritaria = false;
        }
        if (this.qrValidado == null) {
            this.qrValidado = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public Usuario getRecolector() {
        return recolector;
    }

    public void setRecolector(Usuario recolector) {
        this.recolector = recolector;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDateTime getFechaCompletada() {
        return fechaCompletada;
    }

    public void setFechaCompletada(LocalDateTime fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public Boolean getPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(Boolean prioritaria) {
        this.prioritaria = prioritaria;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public Boolean getQrValidado() {
        return qrValidado;
    }

    public void setQrValidado(Boolean qrValidado) {
        this.qrValidado = qrValidado;
    }

    public BigDecimal getLatRecolector() {
        return latRecolector;
    }

    public void setLatRecolector(BigDecimal latRecolector) {
        this.latRecolector = latRecolector;
    }

    public BigDecimal getLngRecolector() {
        return lngRecolector;
    }

    public void setLngRecolector(BigDecimal lngRecolector) {
        this.lngRecolector = lngRecolector;
    }

    public String getIncidenciaDescripcion() {
        return incidenciaDescripcion;
    }

    public void setIncidenciaDescripcion(String incidenciaDescripcion) {
        this.incidenciaDescripcion = incidenciaDescripcion;
    }

    public String getIncidenciaEstado() {
        return incidenciaEstado;
    }

    public void setIncidenciaEstado(String incidenciaEstado) {
        this.incidenciaEstado = incidenciaEstado;
    }

    public String getIncidenciaEvidenciaUrl() {
        return incidenciaEvidenciaUrl;
    }

    public void setIncidenciaEvidenciaUrl(String incidenciaEvidenciaUrl) {
        this.incidenciaEvidenciaUrl = incidenciaEvidenciaUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
