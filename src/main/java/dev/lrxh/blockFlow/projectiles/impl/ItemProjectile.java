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
        ItemStack itemStack = new ItemStack(material);
        UUID uuid = UUID.randomUUID();
        int entityId = EntityLib.getPlatform().getEntityIdProvider().provide(uuid, EntityTypes.ITEM);

        ItemEntityMeta meta = new ItemEntityMeta(entityId, new Metadata(entityId));
        meta.setItem(SpigotConversionUtil.fromBukkitItemStack(itemStack));

        WrapperEntity entity = new WrapperEntity(entityId, uuid, EntityTypes.ITEM, meta);

        for (UUID viewer : viewers) {
            entity.addViewer(viewer);
        }

        Vector3d dir = new Vector3d(location.getDirection().toVector3d().x, location.getDirection().toVector3d().y, location.getDirection().toVector3d().z);
        double speed = 0.3;
        Random rand = new Random();
        Vector3d vel = new Vector3d(
                dir.x * speed + rand.nextGaussian() * 0.02,
                dir.y * speed + rand.nextGaussian() * 0.02,
                dir.z * speed + rand.nextGaussian() * 0.02
        );
        entity.spawn(SpigotConversionUtil.fromBukkitLocation(location));
        entity.setVelocity(vel);
        return entity;
    }
}
