package pe.edu.upc.qhurinet.dtos;

public class IncidenciaRecoleccionDTO {
    private String descripcion;
    private String evidenciaUrl;
    private String estado;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEvidenciaUrl() {
        return evidenciaUrl;
    }

    public void setEvidenciaUrl(String evidenciaUrl) {
        this.evidenciaUrl = evidenciaUrl;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
