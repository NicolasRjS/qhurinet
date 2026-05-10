package pe.edu.upc.qhurinet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.qhurinet.dtos.MatrizDistanciaRequestDTO;
import pe.edu.upc.qhurinet.servicesinterfaces.IGeolocalizacionService;

@RestController
@RequestMapping("/api/geolocalizacion")
public class GeolocalizacionController {
    @Autowired
    private IGeolocalizacionService geolocalizacionService;

    @GetMapping("/distancia")
    public ResponseEntity<?> distancia(@RequestParam("latOrigen") Double latOrigen,
                                       @RequestParam("lngOrigen") Double lngOrigen,
                                       @RequestParam("latDestino") Double latDestino,
                                       @RequestParam("lngDestino") Double lngDestino) {
        return ResponseEntity.ok(geolocalizacionService.distancia(latOrigen, lngOrigen, latDestino, lngDestino));
    }

    @PostMapping("/matriz")
    public ResponseEntity<?> matriz(@RequestBody MatrizDistanciaRequestDTO request) {
        try {
            return ResponseEntity.ok(geolocalizacionService.matriz(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
