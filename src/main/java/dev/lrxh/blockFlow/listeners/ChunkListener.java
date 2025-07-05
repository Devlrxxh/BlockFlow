package dev.lrxh.blockFlow.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ChunkListener extends PacketListenerAbstract {
    private final BlockFlow blockFlow;

    @Override
    public void onPacketSend(PacketSendEvent packet) {

        if (packet.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            Player player = packet.getPlayer();

            WrapperPlayServerChunkData chunkData = new WrapperPlayServerChunkData(packet);
            int chunkX = chunkData.getColumn().getX();
            int chunkZ = chunkData.getColumn().getZ();


            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                if (stage.isChunkInBounds(chunkX, chunkZ)) {
                    packet.setCancelled(true);
                    Bukkit.getScheduler().runTaskAsynchronously(blockFlow.getPlugin(), () -> {
                        stage.sendChunkToPlayer(player, chunkX, chunkZ);
                    });
                    return;
                }
            }
        }

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
