package pe.edu.upc.qhurinet.servicesinterfaces;

import pe.edu.upc.qhurinet.entities.MapaCache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IMapaCacheService {
    List<MapaCache> list();
    MapaCache insert(MapaCache m);
    Optional<MapaCache> listId(UUID id);
    void update(MapaCache m);
    void delete(UUID id);
    Optional<MapaCache> findValid(String cacheKey, LocalDateTime fecha);
    Optional<MapaCache> findByCacheKey(String cacheKey);
}
