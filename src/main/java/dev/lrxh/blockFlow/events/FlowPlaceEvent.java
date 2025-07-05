package dev.lrxh.blockFlow.events;

import dev.lrxh.blockFlow.FlowBlock;
import dev.lrxh.blockFlow.FlowPosition;
import dev.lrxh.blockFlow.FlowStage;
import lombok.Getter;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class FlowPlaceEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Player player;
    private final FlowPosition position;
    private final BlockData blockData;
    private final FlowStage stage;

    public FlowPlaceEvent(Player player, FlowPosition position, BlockData blockData, FlowStage stage) {
        this.player = player;
        this.position = position;
        this.blockData = blockData;
        this.stage = stage;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}