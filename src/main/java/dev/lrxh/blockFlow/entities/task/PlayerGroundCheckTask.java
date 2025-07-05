package dev.lrxh.blockFlow.entities.task;

import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowBlock;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
public class PlayerGroundCheckTask implements Runnable {
    private final BlockFlow blockFlow;

    @Override
    public void run() {
        for (FlowStage stage : blockFlow.getStages()) {
            for (UUID uuid : stage.getWatchers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline() || !stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                FlowPosition position = new FlowPosition(
                        (int) Math.floor(player.getLocation().getX()),
                        (int) Math.floor(player.getLocation().getY()) - 1,
                        (int) Math.floor(player.getLocation().getZ())
                );

                FlowBlock block = stage.getBlockDataAt(position);

                if (block.getBlockData().getMaterial().isSolid()) {
                    player.setAllowFlight(true);
                }
            }
        }
    }
}