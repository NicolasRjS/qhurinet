package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class PromedioRecolectorDTO {
    private UUID idRecolector;
    private String nombreRecolector;
    private Double puntuacionPromedio;
    private Long totalRecolecciones;

    public UUID getIdRecolector() { return idRecolector; }
    public void setIdRecolector(UUID idRecolector) { this.idRecolector = idRecolector; }

    public String getNombreRecolector() { return nombreRecolector; }
    public void setNombreRecolector(String nombreRecolector) { this.nombreRecolector = nombreRecolector; }

    public Double getPuntuacionPromedio() { return puntuacionPromedio; }
    public void setPuntuacionPromedio(Double puntuacionPromedio) { this.puntuacionPromedio = puntuacionPromedio; }

    public Long getTotalRecolecciones() { return totalRecolecciones; }
    public void setTotalRecolecciones(Long totalRecolecciones) { this.totalRecolecciones = totalRecolecciones; }
}
