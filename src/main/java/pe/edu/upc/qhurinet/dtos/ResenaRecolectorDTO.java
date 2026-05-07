package pe.edu.upc.qhurinet.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResenaRecolectorDTO {
    private UUID idCalificacion;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime createdAt;
    private String autor;

    public UUID getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(UUID idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
