package pe.edu.upc.qhurinet.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PerfilRecolectorDTO {
    private UUID idRecolector;
    private String nombre;
    private String email;
    private String telefono;
    private String fotoUrl;
    private String descripcion;
    private Boolean disponible;
    private Boolean verificado;
    private Integer puntosTotales;
    private String nivelParticipacion;
    private Double puntuacionPromedio;
    private Long totalValoraciones;
    private Long totalRecolecciones;
    private List<ResenaRecolectorDTO> comentariosDestacados = new ArrayList<>();

    public UUID getIdRecolector() {
        return idRecolector;
    }

    public void setIdRecolector(UUID idRecolector) {
        this.idRecolector = idRecolector;
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

    public Double getPuntuacionPromedio() {
        return puntuacionPromedio;
    }

    public void setPuntuacionPromedio(Double puntuacionPromedio) {
        this.puntuacionPromedio = puntuacionPromedio;
    }

    public Long getTotalValoraciones() {
        return totalValoraciones;
    }

    public void setTotalValoraciones(Long totalValoraciones) {
        this.totalValoraciones = totalValoraciones;
    }

    public Long getTotalRecolecciones() {
        return totalRecolecciones;
    }

    public void setTotalRecolecciones(Long totalRecolecciones) {
        this.totalRecolecciones = totalRecolecciones;
    }

    public List<ResenaRecolectorDTO> getComentariosDestacados() {
        return comentariosDestacados;
    }

    public void setComentariosDestacados(List<ResenaRecolectorDTO> comentariosDestacados) {
        this.comentariosDestacados = comentariosDestacados;
    }
}
