package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "email", length = 200, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "rol", length = 30, nullable = false)
    private String rol;

    @Column(name = "tipo_cuenta", length = 30, nullable = false)
    private String tipoCuenta;

    @Column(name = "proveedor_auth", length = 20, nullable = false)
    private String proveedorAuth;

    @Column(name = "disponible", nullable = false)
    private Boolean disponible;

    @Column(name = "verificado", nullable = false)
    private Boolean verificado;

    @Column(name = "puntos_totales", nullable = false)
    private Integer puntosTotales;

    @Column(name = "nivel_participacion", length = 30, nullable = false)
    private String nivelParticipacion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Usuario() {
    }

    public Usuario(UUID id, String nombre, String email, String passwordHash, String telefono, String fotoUrl, String descripcion, String rol, String tipoCuenta, String proveedorAuth, Boolean disponible, Boolean verificado, Integer puntosTotales, String nivelParticipacion, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.fotoUrl = fotoUrl;
        this.descripcion = descripcion;
        this.rol = rol;
        this.tipoCuenta = tipoCuenta;
        this.proveedorAuth = proveedorAuth;
        this.disponible = disponible;
        this.verificado = verificado;
        this.puntosTotales = puntosTotales;
        this.nivelParticipacion = nivelParticipacion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime fechaActual = LocalDateTime.now();
        this.createdAt = fechaActual;
        this.updatedAt = fechaActual;
        if (this.rol == null) {
            this.rol = "emisor";
        }
        if (this.proveedorAuth == null) {
            this.proveedorAuth = "local";
        }
        if (this.disponible == null) {
            this.disponible = true;
        }
        if (this.verificado == null) {
            this.verificado = false;
        }
        if (this.puntosTotales == null) {
            this.puntosTotales = 0;
        }
        if (this.nivelParticipacion == null) {
            this.nivelParticipacion = "Bronce";
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getProveedorAuth() {
        return proveedorAuth;
    }

    public void setProveedorAuth(String proveedorAuth) {
        this.proveedorAuth = proveedorAuth;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public Integer getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public String getNivelParticipacion() {
        return nivelParticipacion;
    }

    public void setNivelParticipacion(String nivelParticipacion) {
        this.nivelParticipacion = nivelParticipacion;
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
