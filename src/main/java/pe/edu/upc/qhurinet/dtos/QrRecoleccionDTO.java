package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class QrRecoleccionDTO {
    private UUID idRecoleccion;
    private String codigoQr;
    private Boolean qrValidado;

    public UUID getIdRecoleccion() {
        return idRecoleccion;
    }

    public void setIdRecoleccion(UUID idRecoleccion) {
        this.idRecoleccion = idRecoleccion;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public Boolean getQrValidado() {
        return qrValidado;
    }

    public void setQrValidado(Boolean qrValidado) {
        this.qrValidado = qrValidado;
    }
}
