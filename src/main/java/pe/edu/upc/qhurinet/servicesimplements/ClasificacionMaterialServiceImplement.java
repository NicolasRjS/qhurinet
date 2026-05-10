package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.dtos.ClasificacionMaterialDTO;
import pe.edu.upc.qhurinet.entities.Material;
import pe.edu.upc.qhurinet.servicesinterfaces.IClasificacionMaterialService;
import pe.edu.upc.qhurinet.servicesinterfaces.IMaterialService;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ClasificacionMaterialServiceImplement implements IClasificacionMaterialService {
    private static final Map<String, List<String>> KEYWORDS = Map.of(
            "plastico", List.of("plastico", "botella", "pet", "envase", "tapa", "bolsa"),
            "papel", List.of("papel", "hoja", "cuaderno", "periodico", "revista"),
            "carton", List.of("carton", "caja", "empaque"),
            "vidrio", List.of("vidrio", "botella de vidrio", "frasco"),
            "metal", List.of("metal", "lata", "aluminio", "fierro", "acero"),
            "electronico", List.of("electronico", "cable", "celular", "bateria", "pilas", "cargador")
    );

    @Autowired
    private IMaterialService materialService;

    @Override
    public List<ClasificacionMaterialDTO> clasificar(String texto) {
        String normalizado = normalizar(texto);
        List<Material> materiales = materialService.list();
        List<ClasificacionMaterialDTO> resultado = new ArrayList<>();

        for (Material material : materiales) {
            int coincidencias = score(material, normalizado);
            if (coincidencias <= 0) {
                continue;
            }
            ClasificacionMaterialDTO dto = new ClasificacionMaterialDTO();
            dto.setIdMaterial(material.getId());
            dto.setNombreMaterial(material.getNombre());
            dto.setCategoria(material.getCategoria());
            dto.setDescripcion(material.getDescripcion());
            dto.setPuntosPorKg(material.getPuntosPorKg());
            dto.setCoincidencias(coincidencias);
            dto.setConfianza(Math.min(1.0, coincidencias / 4.0));
            resultado.add(dto);
        }

        resultado.sort(Comparator
                .comparing(ClasificacionMaterialDTO::getCoincidencias, Comparator.reverseOrder())
                .thenComparing(ClasificacionMaterialDTO::getNombreMaterial));
        return resultado;
    }

    private int score(Material material, String texto) {
        int score = 0;
        String nombre = normalizar(material.getNombre());
        String categoria = normalizar(material.getCategoria());
        String descripcion = normalizar(material.getDescripcion());

        if (contiene(texto, nombre)) {
            score += 4;
        }
        if (contiene(texto, categoria)) {
            score += 3;
        }
        for (String keyword : KEYWORDS.getOrDefault(categoria, List.of())) {
            if (contiene(texto, keyword)) {
                score += 1;
            }
        }
        for (String palabra : nombre.split(" ")) {
            if (palabra.length() > 2 && contiene(texto, palabra)) {
                score += 1;
            }
        }
        if (!descripcion.isBlank() && contiene(texto, descripcion)) {
            score += 1;
        }
        return score;
    }

    private boolean contiene(String texto, String valor) {
        return valor != null && !valor.isBlank() && texto.contains(normalizar(valor));
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT).trim();
    }
}
