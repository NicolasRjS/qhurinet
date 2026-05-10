package pe.edu.upc.qhurinet.dtos;

import java.util.List;

public class MatrizDistanciaResponseDTO {
    private String proveedor;
    private List<List<DistanciaDTO>> matriz;

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public List<List<DistanciaDTO>> getMatriz() {
        return matriz;
    }

    public void setMatriz(List<List<DistanciaDTO>> matriz) {
        this.matriz = matriz;
    }
}
