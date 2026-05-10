package pe.edu.upc.qhurinet.dtos;

import java.util.List;
import java.util.UUID;

public class PuntosUsuarioDTO {
    private UUID idUsuario;
    private Integer saldo;
    private List<HistorialPuntosSaldoDTO> historial;

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public List<HistorialPuntosSaldoDTO> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialPuntosSaldoDTO> historial) {
        this.historial = historial;
    }
}
