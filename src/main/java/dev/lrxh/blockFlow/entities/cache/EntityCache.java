package dev.lrxh.blockFlow.entities.cache;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class EntityCache {
    private final Map<Integer, Entity> entities;

    public EntityCache() {
        this.entities = new HashMap<>();
    }

    public List<LivingEntity> getNearbyLivingEntities(Location location, double radius) {
        return entities.values().stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.getLocation().equals(location))
                .filter(entity -> entity.getLocation().distance(location) <= radius)
                .map(entity -> (LivingEntity) entity)
                .collect(Collectors.toList());
    }
}
