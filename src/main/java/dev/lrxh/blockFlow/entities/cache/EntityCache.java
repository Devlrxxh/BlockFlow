package dev.lrxh.blockFlow.entities.cache;

import dev.lrxh.blockFlow.BlockFlow;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Getter
public class EntityCache {

    private final Map<Integer, Entity> entities = new HashMap<>();
    private final BlockFlow blockFlow;

    public EntityCache(BlockFlow blockFlow) {
        this.blockFlow = blockFlow;
    }

    public List<LivingEntity> getNearbyLivingEntities(Location location, double radius) {
        Future<List<LivingEntity>> future = Bukkit.getScheduler().callSyncMethod(blockFlow.getPlugin(), () ->
                findNearbyLivingEntities(location, radius)
        );

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return Collections.emptyList();
        }
    }

    private List<LivingEntity> findNearbyLivingEntities(Location location, double radius) {
        World world = location.getWorld();
        if (world == null) return Collections.emptyList();

        List<LivingEntity> nearbyEntities = new ArrayList<>();
        double radiusSquared = radius * radius;
        int chunkRadius = (int) Math.ceil(radius / 16.0);

        int centerChunkX = location.getBlockX() >> 4;
        int centerChunkZ = location.getBlockZ() >> 4;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;

                if (!world.isChunkLoaded(chunkX, chunkZ)) continue;

                for (Entity entity : world.getChunkAt(chunkX, chunkZ).getEntities()) {
                    if (entity instanceof LivingEntity living) {
                        if (entity.getLocation().distanceSquared(location) <= radiusSquared) {
                            nearbyEntities.add(living);
                        }
                    }
                }
            }
        }

        return nearbyEntities;
    }
}
