package dev.lrxh.blockFlow.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class BlockChangeListener extends PacketListenerAbstract {
    private final BlockFlow blockFlow;

    @Override
    public void onPacketSend(PacketSendEvent packet) {
        if (packet.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange(packet);
            Player player = packet.getPlayer();

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                FlowPosition flowPosition = new FlowPosition(wrapper.getBlockPosition().getX(), wrapper.getBlockPosition().getY(), wrapper.getBlockPosition().getZ());
                if (stage.isPositionInBounds(flowPosition)) {

                    if (wrapper.getBlockState().getType().getName().equalsIgnoreCase(stage.getBlockDataAt(flowPosition).getBlockData().getMaterial().name()))
                        continue;

                    packet.setCancelled(true);
                    return;
                }
            }
        }
    }
}
