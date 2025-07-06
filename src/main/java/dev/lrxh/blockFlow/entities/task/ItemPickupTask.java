package dev.lrxh.blockFlow.entities.task;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCollectItem;
import dev.lrxh.blockFlow.BlockFlow;
import dev.lrxh.blockFlow.events.FlowItemPickupEvent;
import dev.lrxh.blockFlow.stage.FlowStage;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.projectile.ItemEntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ItemPickupTask implements Runnable {

    private final BlockFlow blockFlow;

    public ItemPickupTask(BlockFlow blockFlow) {
        this.blockFlow = blockFlow;
    }

    @Override
    public void run() {
        for (FlowStage stage : blockFlow.getStages()) {
            Iterator<Map.Entry<WrapperEntity, Long>> iterator = stage.getEntities().entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<WrapperEntity, Long> entry = iterator.next();
                WrapperEntity entity = entry.getKey();
                long spawnTime = entry.getValue();

                if (System.currentTimeMillis() - spawnTime < 500) continue;

                ItemEntityMeta meta = (ItemEntityMeta) entity.getEntityMeta();

                for (UUID watcherId : stage.getWatchers()) {
                    Player player = Bukkit.getPlayer(watcherId);
                    if (player == null || !player.isOnline()) continue;

                    double distance = player.getLocation().distance(
                            SpigotConversionUtil.toBukkitLocation(stage.getWorld(), entity.getLocation())
                    );

                    if (distance <= 1.5) {
                        ItemStack item = SpigotConversionUtil.toBukkitItemStack(meta.getItem());

                        FlowItemPickupEvent event = new FlowItemPickupEvent(player, item, stage);
                        event.callEvent();
                        if (event.isCancelled()) continue;

                        player.getInventory().addItem(item);
                        player.playSound(player.getLocation(), "entity.item.pickup", 1.0f, 1.0f);

                        WrapperPlayServerCollectItem collectItemPacket = new WrapperPlayServerCollectItem(
                                entity.getEntityId(),
                                player.getEntityId(),
                                1
                        );

                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, collectItemPacket);

                        entity.remove();
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }
}
