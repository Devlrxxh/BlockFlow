package dev.lrxh.blockFlow.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.events.FlowPlaceEvent;
import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import io.papermc.paper.math.BlockPosition;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

@AllArgsConstructor
public class BlockPlaceListener extends PacketListenerAbstract {
    private final BlockFlow blockFlow;

    @Override
    public void onPacketReceive(PacketReceiveEvent packet) {
        Player player = packet.getPlayer();

        if (packet.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(packet);

            for (FlowStage stage : blockFlow.getStages()) {
                if (!stage.getWatchers().contains(player.getUniqueId())) continue;
                if (!stage.getWorld().equals(player.getWorld())) continue;

                Vector3i clickedBlockPos = wrapper.getBlockPosition();
                BlockFace face = wrapper.getFace();

                FlowPosition position = new FlowPosition(
                        clickedBlockPos.getX() + face.getModX(),
                        clickedBlockPos.getY() + face.getModY(),
                        clickedBlockPos.getZ() + face.getModZ()
                );

                packet.setCancelled(true);

                Location blockLoc = position.toLocation(player.getWorld());
                BoundingBox blockBox = BoundingBox.of(blockLoc, blockLoc.getX() + 1, blockLoc.getY() + 1, blockLoc.getZ() + 1);

                for (Entity entity : blockFlow.getEntityCache().getNearbyLivingEntities(blockLoc, 2)) {
                    if (blockBox.overlaps(entity.getBoundingBox())) {
                        player.sendBlockChange(blockLoc, stage.getBlockDataAt(position).getBlockData());
                        return;
                    }
                }

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


        packet.markForReEncode(false);
    }
}
