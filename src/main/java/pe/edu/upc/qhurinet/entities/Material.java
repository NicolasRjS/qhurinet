package pe.edu.upc.qhurinet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "Material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "nombre", length = 100, nullable = false, unique = true)
    private String nombre;

    @Column(name = "categoria", length = 50, nullable = false)
    private String categoria;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "puntos_por_kg", precision = 6, scale = 2, nullable = false)
    private BigDecimal puntosPorKg;

    public Material() {
    }

    public Material(int id, String nombre, String categoria, String descripcion, BigDecimal puntosPorKg) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.puntosPorKg = puntosPorKg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPuntosPorKg() {
        return puntosPorKg;
    }

    public void setPuntosPorKg(BigDecimal puntosPorKg) {
        this.puntosPorKg = puntosPorKg;
    }
}
