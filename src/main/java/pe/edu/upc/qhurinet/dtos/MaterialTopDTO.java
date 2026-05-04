package pe.edu.upc.qhurinet.dtos;

public class MaterialTopDTO {
    private String nombreMaterial;
    private String categoria;
    private Double totalKg;

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

    public Double getTotalKg() {
        return totalKg;
    }

    public void setTotalKg(Double totalKg) {
        this.totalKg = totalKg;
    }
}