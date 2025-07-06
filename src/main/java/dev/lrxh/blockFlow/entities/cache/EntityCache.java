package dev.lrxh.blockFlow.entities.cache;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class EntityCache {
    private Map<Integer, Entity> entities;

    public EntityCache() {
        this.entities = new HashMap<>();
    }

    public List<Entity> getNearbyLivingEntities(Location location, double radius) {

        double expander = 16.0D;

        double x = location.getX();
        double z = location.getZ();

        int minX = (int) Math.floor((x - radius) / expander);
        int maxX = (int) Math.floor((x + radius) / expander);

        int minZ = (int) Math.floor((z - radius) / expander);
        int maxZ = (int) Math.floor((z + radius) / expander);

        World world = location.getWorld();

        List<Entity> entities = new ArrayList<>();

        for (int xVal = minX; xVal <= maxX; xVal++) {
            for (int zVal = minZ; zVal <= maxZ; zVal++) {
                if (world.isChunkLoaded(xVal, zVal)) {
                    entities.addAll(Arrays.asList(world.getChunkAt(xVal, zVal).getEntities()));
                }
            }
        }

        entities.removeIf(entity -> entity.getLocation().distanceSquared(location) > radius * radius);

        return entities;
    }
}
