package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PublicacionMaterialId implements Serializable {
    @Column(name = "id_publicacion", nullable = false)
    private UUID idPublicacion;

    @Column(name = "id_material", nullable = false)
    private Integer idMaterial;

    public PublicacionMaterialId() {
    }

    public PublicacionMaterialId(UUID idPublicacion, Integer idMaterial) {
        this.idPublicacion = idPublicacion;
        this.idMaterial = idMaterial;
    }

    public UUID getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(UUID idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public Integer getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Integer idMaterial) {
        this.idMaterial = idMaterial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublicacionMaterialId that = (PublicacionMaterialId) o;
        return Objects.equals(idPublicacion, that.idPublicacion) && Objects.equals(idMaterial, that.idMaterial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPublicacion, idMaterial);
    }
}
