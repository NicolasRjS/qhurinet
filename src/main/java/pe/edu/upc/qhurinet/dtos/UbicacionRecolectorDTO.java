package pe.edu.upc.qhurinet.dtos;

import java.math.BigDecimal;

public class UbicacionRecolectorDTO {
    private BigDecimal latRecolector;
    private BigDecimal lngRecolector;

    public BigDecimal getLatRecolector() {
        return latRecolector;
    }

    public void setLatRecolector(BigDecimal latRecolector) {
        this.latRecolector = latRecolector;
    }

    public BigDecimal getLngRecolector() {
        return lngRecolector;
    }

    public void setLngRecolector(BigDecimal lngRecolector) {
        this.lngRecolector = lngRecolector;
    }
}
