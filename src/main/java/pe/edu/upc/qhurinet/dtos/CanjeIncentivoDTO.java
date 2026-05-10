package pe.edu.upc.qhurinet.dtos;

import java.util.UUID;

public class CanjeIncentivoDTO {
    private UUID idUsuario;
    private UUID idIncentivo;

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public UUID getIdIncentivo() {
        return idIncentivo;
    }

    public void setIdIncentivo(UUID idIncentivo) {
        this.idIncentivo = idIncentivo;
    }
}
