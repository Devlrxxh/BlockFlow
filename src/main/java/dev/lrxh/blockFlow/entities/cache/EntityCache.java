package dev.lrxh.blockFlow.entities.cache;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;

@Getter
public class EntityCache {
    private final Map<Integer, Entity> entities;

    public EntityCache() {
        this.entities = new HashMap<>();
    }

    public List<LivingEntity> getNearbyLivingEntities(Location location, double radius) {
        double chunkSize = 16.0D;
        double x = location.getX();
        double z = location.getZ();
        double radiusSquared = radius * radius;

        int minX = (int) Math.floor((x - radius) / chunkSize);
        int maxX = (int) Math.floor((x + radius) / chunkSize);
        int minZ = (int) Math.floor((z - radius) / chunkSize);
        int maxZ = (int) Math.floor((z + radius) / chunkSize);

        World world = location.getWorld();
        if (world == null) return Collections.emptyList();

        List<LivingEntity> nearby = new ArrayList<>();

        for (int xVal = minX; xVal <= maxX; xVal++) {
            for (int zVal = minZ; zVal <= maxZ; zVal++) {
                if (world.isChunkLoaded(xVal, zVal)) {
                    for (Entity entity : world.getChunkAt(xVal, zVal).getEntities()) {
                        if (entity instanceof LivingEntity) {
                            if (entity.getLocation().distanceSquared(location) <= radiusSquared) {
                                nearby.add((LivingEntity) entity);
                            }
                        }
                    }
                }
            }
        }

        return nearby;
    }
}
