package dev.lrxh.blockFlow;

import com.github.retrooper.packetevents.PacketEvents;
import dev.lrxh.blockFlow.listeners.*;
import dev.lrxh.blockFlow.stage.FlowStage;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BlockFlow {
    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final List<FlowStage> stages;

    public BlockFlow(JavaPlugin plugin) {
        this.plugin = plugin;
        this.stages = new ArrayList<>();
        this.plugin.getServer().getPluginManager().registerEvents(new BukkitListener(this), plugin);

        Arrays.asList(
                new ChunkListener(this),
                new BlockBreakListener(this),
                new BlockPlaceListener(this),
                new PlayerMoveListener(this)
        ).forEach(listener -> PacketEvents.getAPI().getEventManager().registerListener(listener));

        PacketEvents.getAPI().init();
    }

    public void addStage(FlowStage stage) {
        stages.add(stage);
    }
}
