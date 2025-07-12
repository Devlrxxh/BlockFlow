package dev.lrxh.blockFlow.projectiles;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.projectiles.impl.IProjectile;
import dev.lrxh.blockFlow.projectiles.impl.ItemProjectile;

import java.util.Map;

public class ProjectileHandler {
    private final BlockFlow blockFlow;
    private final Map<EntityType, IProjectile> projectiles;

    public ProjectileHandler(BlockFlow blockFlow) {
        this.blockFlow = blockFlow;
        this.projectiles = Map.of(
                EntityTypes.ITEM, new ItemProjectile()
        );
    }

    public IProjectile get(EntityType entityType) {
        return projectiles.getOrDefault(entityType, new ItemProjectile());
    }
}
