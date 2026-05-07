package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecoleccionRangoDTO {
    private UUID id;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaCompletada;
    private String estado;
    private String tituloPublicacion;
    private String nombreRecolector;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public LocalDateTime getFechaCompletada() { return fechaCompletada; }
    public void setFechaCompletada(LocalDateTime fechaCompletada) { this.fechaCompletada = fechaCompletada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTituloPublicacion() { return tituloPublicacion; }
    public void setTituloPublicacion(String tituloPublicacion) { this.tituloPublicacion = tituloPublicacion; }

    public String getNombreRecolector() { return nombreRecolector; }
    public void setNombreRecolector(String nombreRecolector) { this.nombreRecolector = nombreRecolector; }
}
