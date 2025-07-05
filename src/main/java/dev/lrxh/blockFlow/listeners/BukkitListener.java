package dev.lrxh.blockFlow.listeners;

import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

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
}

