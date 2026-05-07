package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class IncentivoDisponibleDTO {
    private UUID idIncentivo;
    private String tipo;
    private String nombre;
    private String descripcion;
    private Integer costoPuntos;
    private Integer stock;
    private Boolean activo;
    private Integer puntosUsuario;
    private Boolean puntosSuficientes;
    private Boolean yaRegistrado;

    public UUID getIdIncentivo() {
        return idIncentivo;
    }

    public void setIdIncentivo(UUID idIncentivo) {
        this.idIncentivo = idIncentivo;
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

    public Integer getPuntosUsuario() {
        return puntosUsuario;
    }

    public void setPuntosUsuario(Integer puntosUsuario) {
        this.puntosUsuario = puntosUsuario;
    }

    public Boolean getPuntosSuficientes() {
        return puntosSuficientes;
    }

    public void setPuntosSuficientes(Boolean puntosSuficientes) {
        this.puntosSuficientes = puntosSuficientes;
    }

    public Boolean getYaRegistrado() {
        return yaRegistrado;
    }

    public void setYaRegistrado(Boolean yaRegistrado) {
        this.yaRegistrado = yaRegistrado;
    }
}
