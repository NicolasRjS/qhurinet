package pe.edu.upc.qhurinet.dtos;

import java.util.List;
import java.util.UUID;

public class RutaOptimaResponseDTO {
    private UUID idRuta;
    private String metodo;
    private List<PuntoRutaDTO> puntosOrdenados;
    private Double distanciaTotalKm;
    private Integer tiempoEstimadoMin;
    private String puntosJson;

    public UUID getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(UUID idRuta) {
        this.idRuta = idRuta;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public List<PuntoRutaDTO> getPuntosOrdenados() {
        return puntosOrdenados;
    }

    public void setPuntosOrdenados(List<PuntoRutaDTO> puntosOrdenados) {
        this.puntosOrdenados = puntosOrdenados;
    }

    public Double getDistanciaTotalKm() {
        return distanciaTotalKm;
    }

    public void setDistanciaTotalKm(Double distanciaTotalKm) {
        this.distanciaTotalKm = distanciaTotalKm;
    }

    public Integer getTiempoEstimadoMin() {
        return tiempoEstimadoMin;
    }

    public void setTiempoEstimadoMin(Integer tiempoEstimadoMin) {
        this.tiempoEstimadoMin = tiempoEstimadoMin;
    }

    public String getPuntosJson() {
        return puntosJson;
    }

    public void setPuntosJson(String puntosJson) {
        this.puntosJson = puntosJson;
    }
}
