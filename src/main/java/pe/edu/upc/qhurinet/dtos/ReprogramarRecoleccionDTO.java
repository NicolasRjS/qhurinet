package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;

public class ReprogramarRecoleccionDTO {
    private LocalDateTime fechaProgramada;

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }
}
