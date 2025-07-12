package dev.lrxh.blockFlow.listeners;

import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.events.FlowPlayerItemDropEvent;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
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

        Location dropLocation = player.getEyeLocation().clone().add(0, -0.3, 0);

        Material material = event.getItemDrop().getItemStack().getType();

        for (FlowStage stage : blockFlow.getStages()) {
            if (!stage.getWatchers().contains(player.getUniqueId())) continue;
            FlowPosition pos = new FlowPosition(
                    dropLocation.getBlockX(),
                    dropLocation.getBlockY(),
                    dropLocation.getBlockZ()
            );

            if (stage.isPositionInBounds(pos)) {
                FlowPlayerItemDropEvent dropEvent =
                        new FlowPlayerItemDropEvent(player, pos, material, stage);
                dropEvent.callEvent();
                if (dropEvent.isCancelled()) return;
                event.setCancelled(true);
                player.getInventory().remove(event.getItemDrop().getItemStack());
                stage.dropItem(material, dropLocation, blockFlow);
            }
        }
    }
}

