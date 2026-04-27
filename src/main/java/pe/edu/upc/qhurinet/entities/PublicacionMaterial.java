package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "Publicacion_Material")
public class PublicacionMaterial {
    @EmbeddedId
    private PublicacionMaterialId id;

    @ManyToOne
    @MapsId("idPublicacion")
    @JoinColumn(name = "id_publicacion", nullable = false)
    private Publicacion publicacion;

    @ManyToOne
    @MapsId("idMaterial")
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @Column(name = "cantidad", precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "unidad", length = 20, nullable = false)
    private String unidad;

    public PublicacionMaterial() {
    }

    public PublicacionMaterial(PublicacionMaterialId id, Publicacion publicacion, Material material, BigDecimal cantidad, String unidad) {
        this.id = id;
        this.publicacion = publicacion;
        this.material = material;
        this.cantidad = cantidad;
        this.unidad = unidad;
    }

    public PublicacionMaterialId getId() {
        return id;
    }

    public void setId(PublicacionMaterialId id) {
        this.id = id;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
}
