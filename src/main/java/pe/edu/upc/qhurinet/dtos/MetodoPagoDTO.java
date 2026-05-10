package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class MetodoPagoDTO {
    private UUID id;
    private UUID idUsuario;
    private String tipo;
    private String alias;
    private String titular;
    private String detalleEnmascarado;
    private Boolean principal;
    private Boolean activo;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getDetalleEnmascarado() {
        return detalleEnmascarado;
    }

    public void setDetalleEnmascarado(String detalleEnmascarado) {
        this.detalleEnmascarado = detalleEnmascarado;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
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
