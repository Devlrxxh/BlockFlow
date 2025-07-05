package dev.lrxh.blockFlow;

import com.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class BlockFlow {
    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final List<FlowStage> stages;

    public BlockFlow(JavaPlugin plugin) {
        this.plugin = plugin;
        this.stages = new ArrayList<>();
        this.plugin.getServer().getPluginManager().registerEvents(new BukkitListener(this), plugin);

        PacketEvents.getAPI().getEventManager().registerListener(new ChunkLoadListener(this));
        PacketEvents.getAPI().init();
    }

    public void addStage(FlowStage stage) {
        stages.add(stage);
    }

    public FlowStage getByUUID(UUID uuid) {
        for (FlowStage stage : stages) {
            if (stage.getUuid().equals(uuid)) {
                return stage;
            }
        }
        return null;
    }
}
