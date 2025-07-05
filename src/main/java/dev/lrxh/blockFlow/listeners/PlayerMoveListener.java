package dev.lrxh.blockFlow.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowBlock;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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

                FlowPosition position = new FlowPosition(
                        (int) Math.floor(wrapper.getPosition().getX()),
                        (int) Math.floor(wrapper.getPosition().getY()) - 1,
                        (int) Math.floor(wrapper.getPosition().getZ())
                );

               if (stage.getBlockDataAt(position).getBlockData().getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }

            }
        }

        if (packet.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) {
            WrapperPlayClientPlayerRotation wrapper = new WrapperPlayClientPlayerRotation(packet);

            if (wrapper.isOnGround()) return;

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                FlowPosition position = new FlowPosition(
                        (int) Math.floor(wrapper.getLocation().getX()),
                        (int) Math.floor(wrapper.getLocation().getY()) - 1,
                        (int) Math.floor(wrapper.getLocation().getZ())
                );

               if (stage.getBlockDataAt(position).getBlockData().getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }

            }
        }

        if (packet.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(packet);

            if (wrapper.isOnGround()) return;

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                FlowPosition position = new FlowPosition(
                        (int) Math.floor(wrapper.getLocation().getX()),
                        (int) Math.floor(wrapper.getLocation().getY()) - 1,
                        (int) Math.floor(wrapper.getLocation().getZ())
                );

               if (stage.getBlockDataAt(position).getBlockData().getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }

            }
        }

        packet.markForReEncode(true);
    }

    @Override
    public void onPacketSend(PacketSendEvent packet) {
        Player player = packet.getPlayer();

        if (packet.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove wrapper = new WrapperPlayServerEntityRelativeMove(packet);

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                Entity entity = blockFlow.getEntityCache().getEntities().get(wrapper.getEntityId());

                Location location = entity.getLocation();

                FlowPosition position = new FlowPosition(
                        (int) Math.floor(location.getX()),
                        (int) Math.floor(location.getY()) - 1,
                        (int) Math.floor(location.getZ())
                );

               if (stage.getBlockDataAt(position).getBlockData().getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }
            }
        }

        if (packet.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION) {
            WrapperPlayServerEntityRotation wrapper = new WrapperPlayServerEntityRotation(packet);

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                Entity entity = blockFlow.getEntityCache().getEntities().get(wrapper.getEntityId());

                Location location = entity.getLocation();

                FlowPosition position = new FlowPosition(
                        (int) Math.floor(location.getX()),
                        (int) Math.floor(location.getY()) - 1,
                        (int) Math.floor(location.getZ())
                );

                if (stage.getBlockDataAt(position).getBlockData().getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }
            }
        }

        if (packet.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation wrapper = new WrapperPlayServerEntityRelativeMoveAndRotation(packet);

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) {
                    continue;
                }

                if (!stage.getWorld().equals(player.getWorld())) {
                    continue;
                }

                Entity entity = blockFlow.getEntityCache().getEntities().get(wrapper.getEntityId());

                Location location = entity.getLocation();

                FlowPosition position = new FlowPosition(
                        (int) Math.floor(location.getX()),
                        (int) Math.floor(location.getY()) - 1,
                        (int) Math.floor(location.getZ())
                );

               if (stage.getBlockDataAt(position).getBlockData().getMaterial() != Material.AIR) {
                    wrapper.setOnGround(true);
                }
            }
        }

        packet.markForReEncode(true);
    }
}