package pe.edu.upc.qhurinet.dtos;

import java.util.List;

public class MatrizDistanciaRequestDTO {
    private List<PuntoRutaDTO> origenes;
    private List<PuntoRutaDTO> destinos;

    public List<PuntoRutaDTO> getOrigenes() {
        return origenes;
    }

    public void setOrigenes(List<PuntoRutaDTO> origenes) {
        this.origenes = origenes;
    }

    public List<PuntoRutaDTO> getDestinos() {
        return destinos;
    }

    public void setDestinos(List<PuntoRutaDTO> destinos) {
        this.destinos = destinos;
    }
}
