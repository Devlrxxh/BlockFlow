package dev.lrxh.blockFlow;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import dev.lrxh.blockFlow.events.FlowBreakEvent;
import dev.lrxh.blockFlow.events.FlowPlaceEvent;
import lombok.AllArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@AllArgsConstructor
public class ChunkLoadListener extends PacketListenerAbstract {
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
                FlowBlock block = stage.getBlockDataAt(position);

                if (actionType == DiggingAction.FINISHED_DIGGING || block.canInstantBreak(player)) {
                    packet.setCancelled(true);

                    Bukkit.getScheduler().runTask(blockFlow.getPlugin(), () -> {

                        FlowBreakEvent event = new FlowBreakEvent(player, position, block, stage);
                        event.callEvent();

                        Location location = position.toLocation(player.getWorld());

                        if (event.isCancelled()) {
                            player.sendBlockChange(location, block.getBlockData());
                            stage.setBlockDataAt(position, block.getBlockData());
                        } else {
                            player.sendBlockChange(location, Material.AIR.createBlockData());
                            stage.setBlockDataAt(position, Material.AIR.createBlockData());

                            for (Material drop : block.getDrops()) {
                                Item item = location.getWorld().dropItemNaturally(location, new ItemStack(drop));
                                item.getPersistentDataContainer().set(
                                        new NamespacedKey(blockFlow.getPlugin(),  "blockFlow"),
                                        PersistentDataType.STRING,
                                        stage.getUuid().toString());
                            }

                        }
                    });
                }

            }
        }

        if (packet.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(packet);

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) continue;
                if (!stage.getWorld().equals(player.getWorld())) continue;

                FlowPosition position = new FlowPosition(
                        wrapper.getBlockPosition().getX(),
                        wrapper.getBlockPosition().getY(),
                        wrapper.getBlockPosition().getZ()
                );

                packet.setCancelled(true);

                Bukkit.getScheduler().runTask(blockFlow.getPlugin(), () -> {
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand == null || !itemInHand.getType().isBlock()) return;

                    Material placingMaterial = itemInHand.getType();
                    if (placingMaterial == Material.AIR) return;

                    BlockData blockData = placingMaterial.createBlockData();

                    FlowPlaceEvent event = new FlowPlaceEvent(player, position, blockData, stage);
                    event.callEvent();

                    if (event.isCancelled()) {
                        player.sendBlockChange(position.toLocation(player.getWorld()), stage.getBlockDataAt(position).getBlockData());
                        return;
                    }

                    stage.setBlockDataAt(position, blockData);
                    player.sendBlockChange(position.toLocation(player.getWorld()), blockData);

                    if (player.getGameMode() != GameMode.CREATIVE) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }
                });
            }
        }


        packet.markForReEncode(true);
    }
}
