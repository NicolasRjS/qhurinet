package pe.edu.upc.qhurinet.dtos;

public class DistanciaDTO {
    private Double latOrigen;
    private Double lngOrigen;
    private Double latDestino;
    private Double lngDestino;
    private Double distanciaKm;
    private Integer tiempoEstimadoMin;
    private String proveedor;

    public Double getLatOrigen() {
        return latOrigen;
    }

    public void setLatOrigen(Double latOrigen) {
        this.latOrigen = latOrigen;
    }

    public Double getLngOrigen() {
        return lngOrigen;
    }

    public void setLngOrigen(Double lngOrigen) {
        this.lngOrigen = lngOrigen;
    }

    public Double getLatDestino() {
        return latDestino;
    }

    public void setLatDestino(Double latDestino) {
        this.latDestino = latDestino;
    }

    public Double getLngDestino() {
        return lngDestino;
    }

    public void setLngDestino(Double lngDestino) {
        this.lngDestino = lngDestino;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public Integer getTiempoEstimadoMin() {
        return tiempoEstimadoMin;
    }

    public void setTiempoEstimadoMin(Integer tiempoEstimadoMin) {
        this.tiempoEstimadoMin = tiempoEstimadoMin;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }
}
