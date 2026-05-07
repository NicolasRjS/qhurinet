package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class PublicacionCategoriaDTO {
    private UUID idPublicacion;
    private String titulo;
    private Double latitud;
    private Double longitud;
    private String nombreMaterial;
    private Double cantidad;

    public UUID getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(UUID idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getNombreMaterial() { return nombreMaterial; }
    public void setNombreMaterial(String nombreMaterial) { this.nombreMaterial = nombreMaterial; }

    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) { this.cantidad = cantidad; }
}
