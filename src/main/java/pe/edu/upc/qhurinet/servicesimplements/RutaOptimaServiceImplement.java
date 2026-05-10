package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.dtos.AristaRutaDTO;
import pe.edu.upc.qhurinet.dtos.PuntoRutaDTO;
import pe.edu.upc.qhurinet.dtos.RutaOptimaRequestDTO;
import pe.edu.upc.qhurinet.dtos.RutaOptimaResponseDTO;
import pe.edu.upc.qhurinet.servicesinterfaces.IGeolocalizacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.IRutaOptimaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

@Service
public class RutaOptimaServiceImplement implements IRutaOptimaService {
    @Autowired
    private IGeolocalizacionService geolocalizacionService;

    @Override
    public RutaOptimaResponseDTO calcular(RutaOptimaRequestDTO request) {
        if (request == null || request.getPuntos() == null || request.getPuntos().size() < 2) {
            throw new IllegalArgumentException("Se requieren al menos dos puntos");
        }

        Map<String, PuntoRutaDTO> puntos = normalizarPuntos(request.getPuntos());
        Map<String, List<Edge>> graph = construirGrafo(puntos, request.getAristas());
        String origen = valorInicial(request.getOrigenId(), puntos.keySet().iterator().next());
        validarId(origen, puntos, "origenId");

        RutaCalculada rutaCalculada = request.getDestinoId() == null || request.getDestinoId().isBlank()
                ? rutaVisitandoPuntos(origen, puntos, graph)
                : rutaEntreOrigenDestino(origen, request.getDestinoId(), puntos, graph);

        RutaOptimaResponseDTO response = new RutaOptimaResponseDTO();
        response.setMetodo("dijkstra-local");
        response.setPuntosOrdenados(rutaCalculada.ids().stream().map(puntos::get).toList());
        response.setDistanciaTotalKm(redondear(rutaCalculada.distanciaKm()));
        response.setTiempoEstimadoMin(geolocalizacionService.tiempoEstimadoMin(rutaCalculada.distanciaKm()));
        return response;
    }

    private RutaCalculada rutaEntreOrigenDestino(String origen, String destino, Map<String, PuntoRutaDTO> puntos, Map<String, List<Edge>> graph) {
        validarId(destino, puntos, "destinoId");
        PathResult path = dijkstra(origen, destino, graph);
        return new RutaCalculada(path.ids(), path.distanciaKm());
    }

    private RutaCalculada rutaVisitandoPuntos(String origen, Map<String, PuntoRutaDTO> puntos, Map<String, List<Edge>> graph) {
        List<String> ruta = new ArrayList<>();
        Set<String> pendientes = new HashSet<>(puntos.keySet());
        String actual = origen;
        ruta.add(actual);
        pendientes.remove(actual);
        double distanciaTotal = 0.0;

        while (!pendientes.isEmpty()) {
            PathResult mejor = null;
            String mejorDestino = null;
            for (String candidato : pendientes) {
                PathResult path = dijkstra(actual, candidato, graph);
                if (mejor == null || path.distanciaKm() < mejor.distanciaKm()) {
                    mejor = path;
                    mejorDestino = candidato;
                }
            }
            if (mejor == null || mejorDestino == null) {
                throw new IllegalArgumentException("No existe ruta entre los puntos indicados");
            }
            List<String> segmento = mejor.ids();
            for (int i = 1; i < segmento.size(); i++) {
                String id = segmento.get(i);
                if (!ruta.get(ruta.size() - 1).equals(id)) {
                    ruta.add(id);
                }
                pendientes.remove(id);
            }
            distanciaTotal += mejor.distanciaKm();
            actual = mejorDestino;
        }

        return new RutaCalculada(ruta, distanciaTotal);
    }

    private PathResult dijkstra(String origen, String destino, Map<String, List<Edge>> graph) {
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparing(NodeDistance::distance));

        for (String id : graph.keySet()) {
            dist.put(id, Double.POSITIVE_INFINITY);
        }
        dist.put(origen, 0.0);
        queue.add(new NodeDistance(origen, 0.0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            if (current.distance() > dist.get(current.id())) {
                continue;
            }
            if (current.id().equals(destino)) {
                break;
            }
            for (Edge edge : graph.getOrDefault(current.id(), List.of())) {
                double nextDistance = current.distance() + edge.distanceKm();
                if (nextDistance < dist.getOrDefault(edge.to(), Double.POSITIVE_INFINITY)) {
                    dist.put(edge.to(), nextDistance);
                    prev.put(edge.to(), current.id());
                    queue.add(new NodeDistance(edge.to(), nextDistance));
                }
            }
        }

        if (dist.getOrDefault(destino, Double.POSITIVE_INFINITY).isInfinite()) {
            throw new IllegalArgumentException("No existe ruta entre los puntos indicados");
        }

        List<String> path = new ArrayList<>();
        String current = destino;
        while (current != null) {
            path.add(0, current);
            current = prev.get(current);
        }
        return new PathResult(path, dist.get(destino));
    }

    private Map<String, List<Edge>> construirGrafo(Map<String, PuntoRutaDTO> puntos, List<AristaRutaDTO> aristas) {
        Map<String, List<Edge>> graph = new HashMap<>();
        puntos.keySet().forEach(id -> graph.put(id, new ArrayList<>()));

        if (aristas == null || aristas.isEmpty()) {
            List<String> ids = new ArrayList<>(puntos.keySet());
            for (int i = 0; i < ids.size(); i++) {
                for (int j = i + 1; j < ids.size(); j++) {
                    agregarArista(graph, puntos, ids.get(i), ids.get(j), null);
                }
            }
            return graph;
        }

        for (AristaRutaDTO arista : aristas) {
            if (arista == null || arista.getOrigenId() == null || arista.getDestinoId() == null) {
                continue;
            }
            agregarArista(graph, puntos, arista.getOrigenId(), arista.getDestinoId(), arista.getDistanciaKm());
        }
        return graph;
    }

    private void agregarArista(Map<String, List<Edge>> graph, Map<String, PuntoRutaDTO> puntos, String origenId, String destinoId, Double distanciaKm) {
        validarId(origenId, puntos, "origenId");
        validarId(destinoId, puntos, "destinoId");
        double distancia = distanciaKm == null || distanciaKm <= 0
                ? calcularDistancia(puntos.get(origenId), puntos.get(destinoId))
                : distanciaKm;
        graph.get(origenId).add(new Edge(destinoId, distancia));
        graph.get(destinoId).add(new Edge(origenId, distancia));
    }

    private double calcularDistancia(PuntoRutaDTO origen, PuntoRutaDTO destino) {
        return geolocalizacionService.distanciaKm(origen.getLatitud(), origen.getLongitud(), destino.getLatitud(), destino.getLongitud());
    }

    private Map<String, PuntoRutaDTO> normalizarPuntos(List<PuntoRutaDTO> puntos) {
        Map<String, PuntoRutaDTO> normalizados = new LinkedHashMap<>();
        for (int i = 0; i < puntos.size(); i++) {
            PuntoRutaDTO punto = puntos.get(i);
            if (punto == null || punto.getLatitud() == null || punto.getLongitud() == null) {
                throw new IllegalArgumentException("Cada punto debe tener latitud y longitud");
            }
            String id = punto.getId() == null || punto.getId().isBlank() ? "p" + (i + 1) : punto.getId();
            PuntoRutaDTO copy = new PuntoRutaDTO();
            copy.setId(id);
            copy.setNombre(punto.getNombre());
            copy.setLatitud(punto.getLatitud());
            copy.setLongitud(punto.getLongitud());
            normalizados.put(id, copy);
        }
        return normalizados;
    }

    private void validarId(String id, Map<String, PuntoRutaDTO> puntos, String campo) {
        if (id == null || !puntos.containsKey(id)) {
            throw new IllegalArgumentException(campo + " no corresponde a un punto");
        }
    }

    private String valorInicial(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private double redondear(double value) {
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    private record Edge(String to, double distanceKm) {
    }

    private record NodeDistance(String id, double distance) {
    }

    private record PathResult(List<String> ids, double distanciaKm) {
    }

    private record RutaCalculada(List<String> ids, double distanciaKm) {
    }
}
