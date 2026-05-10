package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "descripcion_imagenes_json", columnDefinition = "TEXT")
    private String descripcionImagenesJson;

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

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(UUID id, String nombre, String email, String username, String passwordHash, String telefono, String fotoUrl, String descripcion, String descripcionImagenesJson, String tipoCuenta, String proveedorAuth, Boolean disponible, Boolean verificado, Integer puntosTotales, String nivelParticipacion, LocalDateTime createdAt, LocalDateTime updatedAt, List<Role> roles) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.fotoUrl = fotoUrl;
        this.descripcion = descripcion;
        this.descripcionImagenesJson = descripcionImagenesJson;
        this.tipoCuenta = tipoCuenta;
        this.proveedorAuth = proveedorAuth;
        this.disponible = disponible;
        this.verificado = verificado;
        this.puntosTotales = puntosTotales;
        this.nivelParticipacion = nivelParticipacion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        setRoles(roles);
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime fechaActual = LocalDateTime.now();
        this.createdAt = fechaActual;
        this.updatedAt = fechaActual;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getDescripcionImagenesJson() {
        return descripcionImagenesJson;
    }

    public void setDescripcionImagenesJson(String descripcionImagenesJson) {
        this.descripcionImagenesJson = descripcionImagenesJson;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles.clear();
        if (roles != null) {
            roles.forEach(this::addRole);
        }
    }

    public void addRole(Role role) {
        role.setUsuario(this);
        this.roles.add(role);
    }

    public void syncRoles(List<String> roleNames) {
        Set<String> normalizedRoles = roleNames.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        this.roles.removeIf(role -> !normalizedRoles.contains(role.getRol()));

        Set<String> currentRoles = this.roles.stream()
                .map(Role::getRol)
                .collect(Collectors.toSet());

        normalizedRoles.stream()
                .filter(roleName -> !currentRoles.contains(roleName))
                .forEach(roleName -> {
                    Role role = new Role();
                    role.setRol(roleName);
                    addRole(role);
                });
    }
}
