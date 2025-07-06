package dev.lrxh.blockFlow.listeners;

import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDropItemEvent;

@AllArgsConstructor
public class BukkitListener implements Listener {
    private final BlockFlow blockFlow;

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        int x = event.getBlock().getX();
        int y = event.getBlock().getY();
        int z = event.getBlock().getZ();

        for (FlowStage flowStage : blockFlow.getStages()) {
            if (flowStage.isPositionInBounds(new FlowPosition(x, y, z))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onItemDrop(EntityDropItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        int x = event.getItemDrop().getLocation().getBlockX();
        int y = event.getItemDrop().getLocation().getBlockY();
        int z = event.getItemDrop().getLocation().getBlockZ();

        for (FlowStage flowStage : blockFlow.getStages()) {
            if (!flowStage.getWatchers().contains(player.getUniqueId())) continue;

            if (flowStage.isPositionInBounds(new FlowPosition(x, y, z))) {
                event.setCancelled(true);
                player.getInventory().remove(event.getItemDrop().getItemStack());

                flowStage.dropItem(event.getItemDrop().getItemStack().getType(),
                        new FlowPosition(x, y, z));
            }
        }
    }
}

