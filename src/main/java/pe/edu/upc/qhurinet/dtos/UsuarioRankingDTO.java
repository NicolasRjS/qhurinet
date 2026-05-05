package pe.edu.upc.qhurinet.dtos;
import java.util.UUID;
public class UsuarioRankingDTO {
    private UUID idUsuario;
    private String nombre;
    private Integer puntosTotales;
    private String nivelParticipacion;
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getPuntosTotales() { return puntosTotales; }
    public void setPuntosTotales(Integer puntosTotales) { this.puntosTotales = puntosTotales; }
    public String getNivelParticipacion() { return nivelParticipacion; }
    public void setNivelParticipacion(String nivelParticipacion) { this.nivelParticipacion = nivelParticipacion; }
}