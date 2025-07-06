package dev.lrxh.blockFlow.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.events.FlowBreakEvent;
import dev.lrxh.blockFlow.events.FlowItemDropEvent;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowBlock;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class BlockBreakListener extends PacketListenerAbstract {
    private final BlockFlow blockFlow;

    @Override
    public void onPacketReceive(PacketReceiveEvent packet) {
        Player player = packet.getPlayer();

        if (packet.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(packet);
            DiggingAction actionType = wrapper.getAction();

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                FlowPosition position = new FlowPosition(wrapper.getBlockPosition().getX(), wrapper.getBlockPosition().getY(), wrapper.getBlockPosition().getZ());

                if (!stage.isPositionInBounds(position)) continue;

                FlowBlock block = stage.getBlockDataAt(position);

                if (actionType == DiggingAction.FINISHED_DIGGING || block.canInstantBreak(player)) {
                    packet.setCancelled(true);

                    Bukkit.getScheduler().runTask(blockFlow.getPlugin(), () -> {

                        FlowBreakEvent event = new FlowBreakEvent(player, position, block, stage);
                        event.callEvent();

                        if (event.isCancelled()) {
                            stage.setBlockDataAt(position, block.getBlockData(), blockFlow.getPlugin());
                        } else {
                            stage.setBlockDataAt(position, Material.AIR.createBlockData(), blockFlow.getPlugin());
                            for (Material material : block.getDrops()) {

                                FlowItemDropEvent dropEvent = new FlowItemDropEvent(player, position, material, stage);
                                dropEvent.callEvent();
                                if (!dropEvent.isCancelled()) {
                                    stage.dropItem(material, position);
                                }
                            }

                        }
                    });
                }

            }
        }

        packet.markForReEncode(true);
    }
}
