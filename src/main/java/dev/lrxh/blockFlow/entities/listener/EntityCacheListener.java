package dev.lrxh.blockFlow.entities.listener;

import dev.lrxh.blockFlow.BlockFlow;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class EntityCacheListener implements Listener {
    private final BlockFlow blockFlow;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        blockFlow.getEntityCache().getEntities().put(entity.getEntityId(), entity);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        blockFlow.getEntityCache().getEntities().put(projectile.getEntityId(), projectile);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        blockFlow.getEntityCache().getEntities().put(item.getEntityId(), item);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        blockFlow.getEntityCache().getEntities().put(player.getEntityId(), player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        blockFlow.getEntityCache().getEntities().remove(entity.getEntityId(), entity);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        blockFlow.getEntityCache().getEntities().remove(player.getEntityId(), player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        blockFlow.getEntityCache().getEntities().remove(projectile.getEntityId(), projectile);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDespawn(ItemDespawnEvent event) {
        Item item = event.getEntity();
        blockFlow.getEntityCache().getEntities().remove(item.getEntityId(), item);
    }
}