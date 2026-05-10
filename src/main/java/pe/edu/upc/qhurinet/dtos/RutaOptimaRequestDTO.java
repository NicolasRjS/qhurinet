package pe.edu.upc.qhurinet.dtos;

import java.util.List;
import java.util.UUID;

public class RutaOptimaRequestDTO {
    private UUID idUsuario;
    private String nombre;
    private String origenId;
    private String destinoId;
    private Boolean guardar;
    private List<PuntoRutaDTO> puntos;
    private List<AristaRutaDTO> aristas;

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getOrigenId() {
        return origenId;
    }

    public void setOrigenId(String origenId) {
        this.origenId = origenId;
    }

    public String getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(String destinoId) {
        this.destinoId = destinoId;
    }

    public Boolean getGuardar() {
        return guardar;
    }

    public void setGuardar(Boolean guardar) {
        this.guardar = guardar;
    }

    public List<PuntoRutaDTO> getPuntos() {
        return puntos;
    }

    public void setPuntos(List<PuntoRutaDTO> puntos) {
        this.puntos = puntos;
    }

    public List<AristaRutaDTO> getAristas() {
        return aristas;
    }

    public void setAristas(List<AristaRutaDTO> aristas) {
        this.aristas = aristas;
    }
}
