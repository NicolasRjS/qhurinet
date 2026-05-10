package pe.edu.upc.qhurinet.dtos;

import java.math.BigDecimal;

public class ClasificacionMaterialDTO {
    private Integer idMaterial;
    private String nombreMaterial;
    private String categoria;
    private String descripcion;
    private BigDecimal puntosPorKg;
    private Integer coincidencias;
    private Double confianza;

    public Integer getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Integer idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getNombreMaterial() {
        return nombreMaterial;
    }

    public void setNombreMaterial(String nombreMaterial) {
        this.nombreMaterial = nombreMaterial;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPuntosPorKg() {
        return puntosPorKg;
    }

    public void setPuntosPorKg(BigDecimal puntosPorKg) {
        this.puntosPorKg = puntosPorKg;
    }

    public Integer getCoincidencias() {
        return coincidencias;
    }

    public void setCoincidencias(Integer coincidencias) {
        this.coincidencias = coincidencias;
    }

    public Double getConfianza() {
        return confianza;
    }

    public void setConfianza(Double confianza) {
        this.confianza = confianza;
    }
}
