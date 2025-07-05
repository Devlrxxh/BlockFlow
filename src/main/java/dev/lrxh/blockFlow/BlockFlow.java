package dev.lrxh.blockFlow;

import com.github.retrooper.packetevents.PacketEvents;
import dev.lrxh.blockFlow.entities.cache.EntityCache;
import dev.lrxh.blockFlow.entities.listener.EntityCacheListener;
import dev.lrxh.blockFlow.entities.task.EntityCacheTask;
import dev.lrxh.blockFlow.entities.task.PlayerGroundCheckTask;
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
    @Getter
    private final EntityCache entityCache;

    public BlockFlow(JavaPlugin plugin) {
        this.plugin = plugin;
        this.stages = new ArrayList<>();
        this.entityCache = new EntityCache();

        Arrays.asList(
                new BukkitListener(this),
                new EntityCacheListener(this)
        ).forEach(listener -> this.plugin.getServer().getPluginManager().registerEvents(listener, plugin));

        Arrays.asList(
                new ChunkListener(this),
                new BlockBreakListener(this),
                new BlockPlaceListener(this),
                new BlockChangeListener(this)
        ).forEach(listener -> PacketEvents.getAPI().getEventManager().registerListener(listener));

        PacketEvents.getAPI().init();

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new EntityCacheTask(this), 0L, 500L);
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new PlayerGroundCheckTask(this), 0L, 40L);

    }

    public void addStage(FlowStage stage) {
        stages.add(stage);
    }
}
