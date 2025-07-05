package dev.lrxh.blockFlow.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowBlock;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class PlayerMoveListener extends PacketListenerAbstract {
    private final BlockFlow blockFlow;

    @Override
    public void onPacketReceive(PacketReceiveEvent packet) {
        Player player = packet.getPlayer();

        if (packet.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            WrapperPlayClientPlayerPosition wrapper = new WrapperPlayClientPlayerPosition(packet);

            if (wrapper.isOnGround()) return;

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }


                FlowPosition position = new FlowPosition(
                        (int) Math.floor(wrapper.getPosition().getX()),
                        (int) Math.floor(wrapper.getPosition().getY()) - 1,
                        (int) Math.floor(wrapper.getPosition().getZ())
                );

                FlowBlock block = stage.getBlockDataAt(position);

                if (block.getBlockData().getMaterial().isSolid()) {
                    wrapper.setOnGround(true);
                }
            }

        }

        if (packet.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            WrapperPlayClientPlayerPositionAndRotation wrapper = new WrapperPlayClientPlayerPositionAndRotation(packet);

            if (wrapper.isOnGround()) return;

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                FlowPosition position = new FlowPosition((int) wrapper.getPosition().getX(), (int) wrapper.getPosition().getY() - 1, (int) wrapper.getPosition().getZ());

                if (stage.getBlockDataAt(position).getBlockData().getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }

            }
        }

        packet.markForReEncode(true);
    }
}
