package pe.edu.upc.qhurinet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.qhurinet.entities.MapaCache;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IMapaCacheRepository extends JpaRepository<MapaCache, UUID> {
    Optional<MapaCache> findTopByCacheKeyAndExpiresAtAfterOrderByCreatedAtDesc(String cacheKey, LocalDateTime fecha);
    Optional<MapaCache> findByCacheKey(String cacheKey);
}
