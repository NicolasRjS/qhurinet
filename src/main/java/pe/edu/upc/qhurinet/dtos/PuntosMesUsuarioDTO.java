package pe.edu.upc.qhurinet.dtos;
import java.util.UUID;
public class PuntosMesUsuarioDTO {
    private UUID idUsuario;
    private String nombre;
    private Integer totalPuntosMes;
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getTotalPuntosMes() { return totalPuntosMes; }
    public void setTotalPuntosMes(Integer totalPuntosMes) { this.totalPuntosMes = totalPuntosMes; }
}
