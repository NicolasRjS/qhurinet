package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialRecoleccionDTO {
    private UUID id;
    private LocalDateTime fechaProgramada;
    private String estado;
    private String tituloPublicacion;
    private String rolUsuario;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTituloPublicacion() { return tituloPublicacion; }
    public void setTituloPublicacion(String tituloPublicacion) { this.tituloPublicacion = tituloPublicacion; }

    public String getRolUsuario() { return rolUsuario; }
    public void setRolUsuario(String rolUsuario) { this.rolUsuario = rolUsuario; }
}
