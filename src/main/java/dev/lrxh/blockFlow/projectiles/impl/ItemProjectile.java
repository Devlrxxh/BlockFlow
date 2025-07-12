package dev.lrxh.blockFlow.projectiles.impl;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.meta.Metadata;
import me.tofaa.entitylib.meta.projectile.ItemEntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class ItemProjectile implements IProjectile {

    @Override
    public WrapperEntity handle(Material material, Location location, Set<UUID> viewers) {
        // Adjust drop position to match vanilla behavior (eye level - 0.3)
        Location dropLocation = location.clone().add(0, -0.3, 0);

        ItemStack itemStack = new ItemStack(material);
        UUID uuid = UUID.randomUUID();
        int entityId = EntityLib.getPlatform().getEntityIdProvider().provide(uuid, EntityTypes.ITEM);

        ItemEntityMeta meta = new ItemEntityMeta(entityId, new Metadata(entityId));
        meta.setItem(SpigotConversionUtil.fromBukkitItemStack(itemStack));

        WrapperEntity entity = new WrapperEntity(entityId, uuid, EntityTypes.ITEM, meta);

        for (UUID viewer : viewers) {
            entity.addViewer(viewer);
        }

        Vector3d velocity = getVanillaDropVelocity();

        entity.spawn(SpigotConversionUtil.fromBukkitLocation(dropLocation));
        entity.setVelocity(velocity);

        return entity;
    }

    private Vector3d getVanillaDropVelocity() {
        Random rand = new Random();

        float f = rand.nextFloat() * 0.5F;
        float angle = rand.nextFloat() * (float) (2 * Math.PI);

        double dx = -Math.sin(angle) * f;
        double dy = 0.2;
        double dz = Math.cos(angle) * f;

        return new Vector3d(dx, dy, dz);
    }
}