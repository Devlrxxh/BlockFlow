package dev.lrxh.blockFlow;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ChunkLoadListener extends PacketListenerAbstract {
    private final BlockFlow blockFlow;

    @Override
    public void onPacketSend(PacketSendEvent event) {

        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            Player player = event.getPlayer();

            WrapperPlayServerChunkData chunkData = new WrapperPlayServerChunkData(event);
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
                    event.setCancelled(true);
                    Bukkit.getScheduler().runTaskAsynchronously(blockFlow.getPlugin(), () -> {
                        stage.sendChunkToPlayer(player, chunkX, chunkZ);
                    });
                    break;
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange(event);
            Player player = event.getPlayer();

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                FlowPosition flowPosition = new FlowPosition(wrapper.getBlockPosition().getX(), wrapper.getBlockPosition().getY(), wrapper.getBlockPosition().getZ());
                if (stage.isPositionInBounds(flowPosition)) {

                    if (wrapper.getBlockState().getType().getName() == stage.getBlockDataAt(flowPosition).getMaterial().name())
                        continue;
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = event.getPlayer();

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            WrapperPlayClientPlayerPosition wrapper = new WrapperPlayClientPlayerPosition(event);

            if (wrapper.isOnGround()) return;

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }


                FlowPosition position = new FlowPosition((int) wrapper.getPosition().getX(), (int) wrapper.getPosition().getY() - 1, (int) wrapper.getPosition().getZ());

                if (stage.getBlockDataAt(position).getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }

            }

        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            WrapperPlayClientPlayerPositionAndRotation wrapper = new WrapperPlayClientPlayerPositionAndRotation(event);

            if (wrapper.isOnGround()) return;

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                FlowPosition position = new FlowPosition((int) wrapper.getPosition().getX(), (int) wrapper.getPosition().getY() - 1, (int) wrapper.getPosition().getZ());

                if (stage.getBlockDataAt(position).getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }

            }
        }

        event.markForReEncode(true);
    }
}
